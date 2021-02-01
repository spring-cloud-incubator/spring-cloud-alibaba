/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.stream.binder.rocketmq.contants;

import org.apache.rocketmq.common.message.MessageConst;

import static org.apache.rocketmq.spring.support.RocketMQHeaders.PREFIX;

/**
 * @author zkzlx
 */
public class RocketMQConst extends MessageConst {

	/**
	 * Header key for RocketMQ Transactional Args.
	 */
	public static final String ROCKET_TRANSACTIONAL_ARG = "TRANSACTIONAL_ARG";

	/**
	 * Default NameServer value.
	 */
	public static final String DEFAULT_NAME_SERVER = "127.0.0.1:9876";

	/**
	 * Default group for SCS RocketMQ Binder.
	 */
	public static final String DEFAULT_GROUP = PREFIX + "binder_default_group_name";

	/**
	 * RocketMQ re-consume times.
	 */
	public static final String ROCKETMQ_RECONSUME_TIMES = PREFIX + "RECONSUME_TIMES";

	public static final String USER_TRANSACTIONAL_ARGS = "TRANSACTIONAL_ARGS";

	/**
	 * It is mainly provided for conversion between rocketMq-message and Spring-message,
	 * and parameters are passed through HEADERS.
	 */
	public static class Headers {
		public static final String KEYS = MessageConst.PROPERTY_KEYS;
		public static final String TAGS = MessageConst.PROPERTY_TAGS;
		public static final String TOPIC = "MQ_TOPIC";
		/**
		 * The ID of the message.
		 */
		public static final String MESSAGE_ID = "MQ_MESSAGE_ID";
		/**
		 * The timestamp that the message producer invokes the message sending API.
		 */
		public static final String BORN_TIMESTAMP = "MQ_BORN_TIMESTAMP";
		/**
		 * The IP and port number of the message producer
		 */
		public static final String BORN_HOST = "MQ_BORN_HOST";

		/**
		 * Message flag, MQ is not processed and is available for use by applications.
		 */
		public static final String FLAG = "MQ_FLAG";
		/**
		 * Message consumption queue ID
		 */
		public static final String QUEUE_ID = "MQ_QUEUE_ID";
		/**
		 * Message system Flag, such as whether or not to compress, whether or not to
		 * transactional messages.
		 */
		public static final String SYS_FLAG = "MQ_SYS_FLAG";
		/**
		 * The transaction ID of the transaction message.
		 */
		public static final String TRANSACTION_ID = "MQ_TRANSACTION_ID";
	}

}