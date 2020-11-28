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
package io.xream.x7.repository;

import io.xream.sqli.core.RepositoryManagement;
import io.xream.sqli.spi.IdGenerator;
import io.xream.x7.repository.id.IdGeneratorPolicy;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * @Author Sim
 */
public class IdGeneratorBootListener implements
        ApplicationListener<ApplicationStartedEvent> {


    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {

        IdGeneratorPolicy idGeneratorPolicy = null;
        try {
            idGeneratorPolicy = applicationStartedEvent.getApplicationContext().getBean(IdGeneratorPolicy.class);
        } catch (Exception e) {

        }

        IdGeneratorBoot.onStarted(idGeneratorPolicy);
    }
}
