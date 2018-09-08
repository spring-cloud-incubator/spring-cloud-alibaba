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

package org.springframework.cloud.alibaba.sentinel;

import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.adapter.servlet.config.WebServletConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author xiaojing
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "spring.cloud.sentinel.enabled", matchIfMissing = true)
@EnableConfigurationProperties(SentinelProperties.class)
public class SentinelWebAutoConfiguration {

    private static final Logger logger = LoggerFactory
            .getLogger(SentinelWebAutoConfiguration.class);

    @Autowired
    private SentinelProperties properties;

    @Autowired
    @Qualifier
    private Optional<UrlBlockHandler> urlBlockHandlerOptional;

    @Autowired
    @Qualifier
    private Optional<UrlCleaner> urlCleanerOptional;

    @PostConstruct
    public void init() {

        if (urlBlockHandlerOptional.isPresent()) {
            WebCallbackManager.setUrlBlockHandler(urlBlockHandlerOptional.get());
        }

        if (urlCleanerOptional.isPresent()) {
            WebCallbackManager.setUrlCleaner(urlCleanerOptional.get());
        }

        if (StringUtils.hasText(properties.getServlet().getBlockPage())) {
            WebServletConfig.setBlockPage(properties.getServlet().getBlockPage());
        }

    }

    @Bean
    public FilterRegistrationBean servletRequestListener() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();

        SentinelProperties.Filter filterConfig = properties.getFilter();

        if (null == filterConfig) {
            filterConfig = new SentinelProperties.Filter();
            properties.setFilter(filterConfig);
        }

        if (filterConfig.getUrlPatterns() == null
                || filterConfig.getUrlPatterns().isEmpty()) {
            List<String> defaultPatterns = new ArrayList<>();
            defaultPatterns.add("/*");
            filterConfig.setUrlPatterns(defaultPatterns);
        }

        registration.addUrlPatterns(filterConfig.getUrlPatterns().toArray(new String[0]));
        Filter filter = new CommonFilter();
        registration.setFilter(filter);
        registration.setOrder(filterConfig.getOrder());
        logger.info("[Sentinel Starter] register Sentinel with urlPatterns: {}.",
                filterConfig.getUrlPatterns());
        return registration;

    }

}