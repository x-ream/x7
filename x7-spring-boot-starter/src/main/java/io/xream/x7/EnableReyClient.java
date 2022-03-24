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
package io.xream.x7;


import io.xream.rey.spring.beanconfiguration.ClientHeaderInterceptorPostProcessor;
import io.xream.rey.spring.beanconfiguration.RestTemplateConfig;
import io.xream.rey.spring.beanconfiguration.ReyClientConfig;
import io.xream.x7.rey.configure.ReyPropertiesConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({
        ReyPropertiesConfiguration.class,
        RestTemplateConfig.class,
        ReyClientConfig.class,
        ReyClientBeanRegistrar.class,
        ClientHeaderInterceptorPostProcessor.class
})
public @interface EnableReyClient {
    String[] basePackages() default {};
}
