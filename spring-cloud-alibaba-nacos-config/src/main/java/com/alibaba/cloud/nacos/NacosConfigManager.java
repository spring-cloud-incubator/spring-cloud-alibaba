/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Objects;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 */
public class NacosConfigManager implements ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(NacosConfigManager.class);

	private static ConfigService service = null;

	private static final CacheableEventPublishingNacosServiceFactory SERVICE_FACTORY = CacheableEventPublishingNacosServiceFactory
			.getSingleton();

	@Autowired
	private NacosConfigProperties properties;

	public ConfigService getConfigService() {
		if (Objects.isNull(service)) {
			try {
				// Using cache object creation factory
				service = SERVICE_FACTORY
						.createConfigService(properties.getConfigServiceProperties());
				properties.initConfigService(service);
			}
			catch (NacosException e) {
				log.error("create config service error!properties={},e=,", properties, e);
			}
		}
		return service;
	}

	// Whenever the Context refresh NacosServiceFactory need change perception

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SERVICE_FACTORY.setApplicationContext(applicationContext);
	}
}
