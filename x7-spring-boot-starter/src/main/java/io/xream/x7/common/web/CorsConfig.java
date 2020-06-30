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
package io.xream.x7.common.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;


public class CorsConfig {

    protected static String[] arr;
    @ConditionalOnMissingBean(CorsFilter.class)
    @Bean
    public CorsFilter corsFilter(Environment environment) {

        String origins = null;
        if (arr != null || arr.length > 0) {
            String str = "";
            int length = arr.length;
            for (int i = 0; i < length; i++) {
                String value = environment.resolvePlaceholders(arr[i]);
                str += value;
                if (i < length - 1) {
                    str += ",";
                }
            }
            origins = str;
        }

        CorsFilter corsFilter = new CorsFilter();
        corsFilter.setOrigins(origins);
        return corsFilter;
    }

    @Bean
    public FilterRegistrationBean corsFilterRegistration(CorsFilter corsFilter) {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(corsFilter);
        registration.addUrlPatterns("/*");
        registration.setName(CorsFilter.class.getSimpleName());
        registration.setOrder(1);
        return registration;
    }

}