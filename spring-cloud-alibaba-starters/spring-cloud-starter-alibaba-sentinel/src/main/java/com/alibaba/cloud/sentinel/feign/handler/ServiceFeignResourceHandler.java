/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.sentinel.feign.handler;

import feign.MethodMetadata;
import feign.Target;

/**
 * 基于服务的限制策略.
 *
 * @author <a href="a11en.huang@foxmail.com">Allen Huang</a>
 */
public class ServiceFeignResourceHandler implements FeignResourceHandler {

	@Override
	public String makeResourceName(Target<?> target, MethodMetadata methodMetadata) {
		if (target instanceof Target.HardCodedTarget) {
			Target.HardCodedTarget<?> hardCodedTarget = (Target.HardCodedTarget<?>) target;
			String serviceName = hardCodedTarget.name();
			// default resource name is HTTP#serviceName
			return String.join("#", "HTTP", serviceName);
		}
		return null;
	}

	@Override
	public String handlerType() {
		return ResourceHandlerHolder.SERVICE_INSTANCE;
	}

}
