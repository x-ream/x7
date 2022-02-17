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

import io.opentracing.Tracer;
import io.xream.x7.reyc.api.ClientHeaderInterceptor;
import io.xream.x7.reyc.api.ReyTemplate;
import io.xream.x7.reyc.api.SimpleRestTemplate;
import io.xream.x7.reyc.internal.DefaultRestTemplate;
import io.xream.x7.reyc.internal.HttpClientResolver;
import io.xream.x7.reyc.internal.HttpProperties;
import io.xream.x7.reyc.internal.ReyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author Rolyer Luo
 */
@Import({HttpProperties.class, ReyProperties.class})
public class ReyClientConfig  {


    @Bean
    public SimpleRestTemplate simpleRestTemplate(ReyTemplate reyTemplate, Tracer tracer) {
        DefaultRestTemplate simpleRestTemplate = new DefaultRestTemplate();
        HttpClientResolver.init(reyTemplate, simpleRestTemplate);

        ClientHeaderInterceptor interceptor = new TracingClientHeaderInterceptor(tracer);
        simpleRestTemplate.headerInterceptor(interceptor);

        return simpleRestTemplate;
    }

}
