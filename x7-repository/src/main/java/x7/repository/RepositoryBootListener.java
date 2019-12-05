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
package x7.repository;

import org.springframework.context.ConfigurableApplicationContext;
import x7.core.async.CasualWorker;
import x7.repository.id.IdGeneratorPolicy;

import java.util.List;


public class RepositoryBootListener {


    public static void onStarted(ConfigurableApplicationContext applicationContext){

        Repository repository = applicationContext.getBean(Repository.class);
        IdGeneratorPolicy idGeneratorPolicy = applicationContext.getBean(IdGeneratorPolicy.class);

        HealthChecker.onStarted();
        CasualWorker.accept(() -> {
            try {
                Thread.sleep(1000);
                IdGenerator obj = null;
                try {
                    obj = IdGenerator.class.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                List<IdGenerator> idGeneratorList = repository.list(obj);
                idGeneratorPolicy.onStart(idGeneratorList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



}
