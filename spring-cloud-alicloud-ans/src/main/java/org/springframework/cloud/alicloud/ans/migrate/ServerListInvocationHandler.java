package org.springframework.cloud.alicloud.ans.migrate;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.alicloud.ans.ribbon.AnsServer;
import org.springframework.cloud.alicloud.ans.ribbon.AnsServerList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 */
class ServerListInvocationHandler implements MethodInterceptor {

	private final static Log log = LogFactory.getLog(ServerListInvocationHandler.class);

	private final static ConcurrentMap<String, ConcurrentMap<String, ServerWraper>> CALL_SERVICE_COUNT = new ConcurrentHashMap<>();
	private final static Set<String> INTERCEPTOR_METHOD_NAME = new ConcurrentSkipListSet();

	private IClientConfig clientConfig;
	private AnsServerList ansServerList;
	private AtomicBoolean isFirst = new AtomicBoolean(false);

	static {
		INTERCEPTOR_METHOD_NAME.add("getInitialListOfServers");
		INTERCEPTOR_METHOD_NAME.add("getUpdatedListOfServers");
	}

	ServerListInvocationHandler(IClientConfig clientConfig) {
		this.clientConfig = clientConfig;
		this.ansServerList = new AnsServerList(clientConfig.getClientName());
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String methodName = invocation.getMethod().getName();
		// step 1: check the method is not interceptor
		if (!INTERCEPTOR_METHOD_NAME.contains(methodName)) {
			return invocation.proceed();
		}

		// step 2: interceptor the method
		List<Server> serverList = (List<Server>) invocation.proceed();
		long s = System.currentTimeMillis();
		log.info("[ START ] merage server list for " + clientConfig.getClientName());
		serverList = merageAnsServerList(serverList);
		log.info("[ END ] merage server list for " + clientConfig.getClientName()
				+ ", cost time " + (System.currentTimeMillis() - s) + " ms .");
		return serverList;
	}

	/**
	 * 后台线程 和 Eureka 两个注册中心进行 Merage 的时候，List 表中始终保持是有效的 Server. 即不考虑 ANS 客户端本地容灾的情况
	 */
	private List<Server> merageAnsServerList(final List<Server> source) {
		if (isFirst.compareAndSet(false, true)) {
			return source;
		}

		// step 1: get all of server list and filter the alive
		List<AnsServer> ansServerList = filterAliveAnsServer(
				this.ansServerList.getInitialListOfServers());

		if (ansServerList.isEmpty()) {
			return source;
		}

		log.info("[" + this.clientConfig.getClientName() + "] Get Server List from ANS:"
				+ ansServerList + "; loadbalancer server list override before:" + source);

		// step 2:remove servers of that have in load balancer list
		final Iterator<Server> serverIterator = source.iterator();
		while (serverIterator.hasNext()) {
			final Server server = serverIterator.next();
			ansServerList.forEach(ansServer -> {
				if (server.getHostPort()
						.equals(ansServer.getHealthService().toInetAddr())) {
					// fix bug: mast be set the zone, update server list,will filter
					// by: ZoneAffinityPredicate
					ansServer.setZone(server.getZone());
					ansServer.setSchemea(server.getScheme());
					ansServer.setId(server.getId());
					ansServer.setReadyToServe(true);
					serverIterator.remove();
					log.info("Source Server is remove " + server.getHostPort()
							+ ", and from ANS Server is override："
							+ ansServer.toString());
				}
			});
		}

		ansServerList.forEach(ansServer -> source.add(ansServer));
		log.info("[" + this.clientConfig.getClientName() + "] "
				+ "; loadbalancer server list override after:" + source);
		// override
		return source;
	}

	private List<AnsServer> filterAliveAnsServer(List<AnsServer> sourceServerList) {
		final List<AnsServer> resultServerList = new LinkedList<>();
		sourceServerList.forEach(ansServer -> {
			boolean isAlive = ansServer.isAlive(3);
			if (isAlive) {
				resultServerList.add(ansServer);
			}
			log.warn(ansServer.toString() + " isAlive :" + isAlive);
		});
		return resultServerList;
	}

	static Map<String, ConcurrentMap<String, ServerWraper>> getServerRegistry() {

		return Collections.unmodifiableMap(CALL_SERVICE_COUNT);
	}

	static void incrementCallService(String serviceId, Server server) {
		ConcurrentMap<String, ServerWraper> concurrentHashMap = CALL_SERVICE_COUNT
				.putIfAbsent(serviceId, new ConcurrentHashMap<>());

		if (concurrentHashMap == null) {
			concurrentHashMap = CALL_SERVICE_COUNT.get(serviceId);
		}

		String ipPort = server.getHostPort();
		ServerWraper serverWraper = concurrentHashMap.putIfAbsent(ipPort,
				new ServerWraper(server, new AtomicLong()));

		if (serverWraper == null) {
			serverWraper = concurrentHashMap.get(ipPort);
		}
		serverWraper.setServer(server);
		serverWraper.getCallCount().incrementAndGet();
	}
}