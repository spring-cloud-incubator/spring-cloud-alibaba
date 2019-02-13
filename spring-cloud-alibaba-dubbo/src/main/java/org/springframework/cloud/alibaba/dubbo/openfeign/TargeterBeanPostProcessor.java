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
package org.springframework.cloud.alibaba.dubbo.openfeign;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.alibaba.dubbo.metadata.repository.DubboServiceMetadataRepository;
import org.springframework.core.env.Environment;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.springframework.util.ClassUtils.getUserClass;
import static org.springframework.util.ClassUtils.resolveClassName;

/**
 * org.springframework.cloud.openfeign.Targeter {@link BeanPostProcessor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 */
public class TargeterBeanPostProcessor implements BeanPostProcessor, BeanClassLoaderAware {

    private static final String TARGETER_CLASS_NAME = "org.springframework.cloud.openfeign.Targeter";

    private final Environment environment;

    private final DubboServiceMetadataRepository dubboServiceMetadataRepository;

    private ClassLoader classLoader;

    public TargeterBeanPostProcessor(Environment environment,
                                     DubboServiceMetadataRepository dubboServiceMetadataRepository) {
        this.environment = environment;
        this.dubboServiceMetadataRepository = dubboServiceMetadataRepository;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
        Class<?> beanClass = getUserClass(bean.getClass());
        Class<?> targetClass = resolveClassName(TARGETER_CLASS_NAME, classLoader);
        if (targetClass.isAssignableFrom(beanClass)) {
            return newProxyInstance(classLoader, new Class[]{targetClass},
                    new TargeterInvocationHandler(bean, environment, dubboServiceMetadataRepository));
        }
        return bean;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}