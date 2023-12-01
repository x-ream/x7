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
package io.xream.x7.listener;

import io.xream.sqli.core.RepositoryManagement;
import io.xream.x7.repository.id.IdGeneratorService;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;


/**
 * @author Sim
 */
public class IdGeneratorBootListener implements
        ApplicationListener<ApplicationStartedEvent> {


    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {

        IdGeneratorService idGeneratorService = null;
        try {
            StringRedisTemplate stringRedisTemplate = applicationStartedEvent.getApplicationContext().getBean(StringRedisTemplate.class);
            idGeneratorService = applicationStartedEvent.getApplicationContext().getBean(IdGeneratorService.class);
            if (idGeneratorService.getStringRedisTemplate() == null) {
                idGeneratorService.setStringRedisTemplate(stringRedisTemplate);
                idGeneratorService.onStart(RepositoryManagement.REPOSITORY_LIST);
            }else {
                idGeneratorService.setStringRedisTemplate(stringRedisTemplate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
