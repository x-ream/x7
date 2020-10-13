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

import io.xream.sqli.api.NativeRepository;
import io.xream.sqli.dialect.Dialect;
import io.xream.sqli.repository.init.SqlInit;
import io.xream.sqli.spi.JdbcHelper;
import io.xream.sqli.spi.L2CacheResolver;
import io.xream.sqli.spi.L2CacheStorage;
import io.xream.sqli.starter.SqliListener;
import io.xream.x7.cache.*;
import io.xream.x7.cache.customizer.L2CacheConsistencyCustomizer;
import io.xream.x7.cache.customizer.L2CacheStorageCustomizer;
import io.xream.x7.cache.customizer.L3CacheArgsToStringCustomizer;
import io.xream.x7.cache.customizer.L3CacheStorageCustomizer;
import io.xream.x7.lock.DistributionLock;
import io.xream.x7.lock.LockProvider;
import io.xream.x7.lock.customizer.LockProviderCustomizer;
import io.xream.x7.repository.IdGeneratorBootListener;
import io.xream.x7.repository.id.IdGeneratorPolicy;
import io.xream.x7.repository.id.IdGeneratorService;
import io.xream.x7.repository.id.customizer.IdGeneratorPolicyCustomizer;
import io.xream.x7.repository.jdbctemplate.JdbcTemplateHelper;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;


public class RepositoryListener implements
        ApplicationListener<ApplicationStartedEvent> {


    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {

        customizeLockProvider(applicationStartedEvent);

        customizeL3CacheArgsToString(applicationStartedEvent);
        customizeL3CacheStorage(applicationStartedEvent);

        if (!X7Data.isEnabled)
            return;

        customizeCacheStorage(applicationStartedEvent);

        customizeL2CacheConsistency(applicationStartedEvent);

        customizeIdGeneratorPolicy(applicationStartedEvent);

        onJdbcHelperCreated(applicationStartedEvent);
        onStarted(applicationStartedEvent);

        IdGeneratorBootListener.onStarted(applicationStartedEvent.getApplicationContext());

    }

    private void onStarted(ApplicationStartedEvent applicationStartedEvent){
        NativeRepository nativeRepository = applicationStartedEvent.getApplicationContext().getBean(NativeRepository.class);
        Dialect dialect = applicationStartedEvent.getApplicationContext().getBean(Dialect.class);
        SqlInit sqlInit = applicationStartedEvent.getApplicationContext().getBean(SqlInit.class);
        SqliListener.onStarted(nativeRepository,dialect,sqlInit);
    }

    private void customizeLockProvider(ApplicationStartedEvent applicationStartedEvent) {
        LockProviderCustomizer customizer = null;
        try {
            customizer = applicationStartedEvent.getApplicationContext().getBean(LockProviderCustomizer.class);
        } catch (Exception e) {
        }

        LockProvider lockProvider = null;
        if (customizer != null && customizer.customize() != null) {
            lockProvider = customizer.customize();
        } else {
            try {
                lockProvider = applicationStartedEvent.getApplicationContext().getBean(LockProvider.class);
            } catch (Exception e) {
            }
        }

        DistributionLock.init(lockProvider);
    }

    private void customizeL3CacheStorage(ApplicationStartedEvent applicationStartedEvent) {

        L3CacheAspect bean = null;
        try {
            bean = applicationStartedEvent.getApplicationContext().getBean(L3CacheAspect.class);
        } catch (Exception e) {
        }
        if (bean == null)
            return;

        L3CacheStorageCustomizer customizer = null;
        try {
            customizer = applicationStartedEvent.getApplicationContext().getBean(L3CacheStorageCustomizer.class);
        } catch (Exception e) {
        }

        L3CacheResolver resolver;

        if (customizer != null && customizer.customize() != null) {
            final L3CacheStorage storage = customizer.customize();
            resolver = () -> storage;
        } else {
            try {
                final L3CacheStorage storage = applicationStartedEvent.getApplicationContext().getBean(L3CacheStorage.class);
                resolver = () -> storage;
            } catch (Exception e) {
                resolver = () -> null;
            }
        }

        bean.setResolver(resolver);

    }

    private void customizeL3CacheArgsToString(ApplicationStartedEvent applicationStartedEvent) {

        try {
            L3CacheAspect bean = applicationStartedEvent.getApplicationContext().getBean(L3CacheAspect.class);
            if (bean == null)
                return;

            L3CacheArgsToStringCustomizer customizer = null;
            try {
                customizer = applicationStartedEvent.getApplicationContext().getBean(L3CacheArgsToStringCustomizer.class);
            } catch (Exception e) {

            }

            ArgsToString argsToString = null;
            if (customizer != null && customizer.customize() !=null) {
                argsToString = customizer.customize();
            } else {
                argsToString = new DefaultArgsToString();
            }
            bean.setArgsToString(argsToString);
        } catch (Exception e) {

        }

    }

    private void onJdbcHelperCreated(ApplicationStartedEvent applicationStartedEvent) {

        SqliListener.onBeanCreated(()->{
            JdbcTemplate jdbcTemplate = null;
            try {
                jdbcTemplate = applicationStartedEvent.getApplicationContext().getBean(JdbcTemplate.class);
            }catch (Exception e) {

            }

            JdbcHelper jdbcHelper = null;
            try{
                jdbcHelper = applicationStartedEvent.getApplicationContext().getBean(JdbcHelper.class);
                JdbcTemplateHelper jth = (JdbcTemplateHelper) jdbcHelper;
                jth.setJdbcTemplate(jdbcTemplate);
            }catch (Exception e){

            }
            return true;
        });


    }

    private void customizeCacheStorage(ApplicationStartedEvent applicationStartedEvent) {

        L2CacheStorageCustomizer customizer = null;
        try {
            customizer = applicationStartedEvent.getApplicationContext().getBean(L2CacheStorageCustomizer.class);
        } catch (Exception e) {

        }

        L2CacheStorage cacheStorage = null;
        if (customizer != null && customizer.customize() != null) {
            cacheStorage = customizer.customize();
        } else {
            try {
                cacheStorage = applicationStartedEvent.getApplicationContext().getBean(L2CacheStorage.class);
            } catch (Exception e) {

            }
        }

        L2CacheResolver levelTwoCacheResolver = applicationStartedEvent.getApplicationContext().getBean(L2CacheResolver.class);
        if (levelTwoCacheResolver == null)
            return;
        levelTwoCacheResolver.setCacheStorage(cacheStorage);

    }

    private void customizeL2CacheConsistency(ApplicationStartedEvent applicationStartedEvent) {
        L2CacheConsistencyCustomizer customizer = null;
        try {
            customizer = applicationStartedEvent.getApplicationContext().getBean(L2CacheConsistencyCustomizer.class);
        } catch (Exception e) {

        }

        if (customizer == null || customizer.customize() == null)
            return;

        L2CacheResolver l2CacheResolver = applicationStartedEvent.getApplicationContext().getBean(L2CacheResolver.class);
        if (l2CacheResolver == null)
            return;
        SqliListener.onL2CacheEnabled(l2CacheResolver, customizer.customize());
    }


    private void customizeIdGeneratorPolicy(ApplicationStartedEvent applicationStartedEvent) {
        IdGeneratorPolicyCustomizer customizer = null;
        try {
            customizer = applicationStartedEvent.getApplicationContext().getBean(IdGeneratorPolicyCustomizer.class);
        } catch (Exception e) {

        }

        IdGeneratorPolicy idGeneratorPolicy = null;
        if (customizer != null && customizer.customize() != null) {
            idGeneratorPolicy = customizer.customize();
        }else{
            try {
                idGeneratorPolicy = applicationStartedEvent.getApplicationContext().getBean(IdGeneratorPolicy.class);
            }catch (Exception e){

            }
        }

        if (idGeneratorPolicy == null)
            return;

        IdGeneratorService service = applicationStartedEvent.getApplicationContext().getBean(IdGeneratorService.class);
        if (service == null)
            return;
        service.setIdGeneratorPolicy(idGeneratorPolicy);

    }

}
