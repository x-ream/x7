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

import io.xream.x7.reyc.api.SimpleRestTemplate;
import io.xream.x7.reyc.api.custom.RestTemplateCustomizer;
import io.xream.x7.reyc.internal.HttpClientResolver;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

public class ReyListener implements
        ApplicationListener<ApplicationStartedEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        customizeRestTemplate(event);
    }

    private void customizeRestTemplate(ApplicationStartedEvent event) {

        try {
            RestTemplateCustomizer bean = event.getApplicationContext().getBean(RestTemplateCustomizer.class);
            if (bean == null)
                return;
            SimpleRestTemplate simpleRestTemplate = bean.customize();
            if (simpleRestTemplate == null)
                return;
            HttpClientResolver.setRestTemplate(simpleRestTemplate);
        }catch (Exception e) {

        }
    }
}
