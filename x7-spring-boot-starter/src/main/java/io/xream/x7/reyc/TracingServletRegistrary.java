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
package io.xream.x7.reyc;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.servlet.BraveServletFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

public class TracingServletRegistrary {

    @ConditionalOnMissingBean(BraveServletFilter.class)
    @ConditionalOnBean(Brave.class)
    @Bean
    public BraveServletFilter braveServletFilter(Brave brave) {
        return new BraveServletFilter(brave.serverRequestInterceptor(), brave.serverResponseInterceptor(),
                new DefaultSpanNameProvider());
    }

    @Bean
    public FilterRegistrationBean braveServletFilterRegistration(BraveServletFilter braveServletFilter) {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(braveServletFilter);
        registration.addUrlPatterns("/*");
        registration.setName(BraveServletFilter.class.getSimpleName());
        registration.setOrder(10);
        return registration;
    }
}
