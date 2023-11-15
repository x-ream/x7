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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.xream.sqli.api.NativeRepository;
import io.xream.sqli.api.TemporaryRepository;
import io.xream.sqli.api.customizer.DialectCustomizer;
import io.xream.sqli.api.customizer.EnumSupportCustomizer;
import io.xream.sqli.core.IdGenerator;
import io.xream.sqli.dialect.Dialect;
import io.xream.sqli.repository.init.SqlInit;
import io.xream.sqli.repository.internal.DefaultEnumSupport;
import io.xream.sqli.repository.internal.DefaultRepositoryX;
import io.xream.sqli.spi.JdbcHelper;
import io.xream.sqli.spi.L2CacheResolver;
import io.xream.sqli.spi.L2CacheStorage;
import io.xream.sqli.spi.Schema;
import io.xream.sqli.spi.customizer.L2CacheConsistencyCustomizer;
import io.xream.sqli.spi.customizer.L2CacheStorageCustomizer;
import io.xream.sqli.starter.EnumSupportListener;
import io.xream.sqli.starter.SqliListener;
import io.xream.sqli.starter.TemporaryTableParserListener;
import io.xream.sqli.util.SqliJsonUtil;
import io.xream.x7.X7Data;
import io.xream.x7.lock.DistributionLock;
import io.xream.x7.lock.LockProvider;
import io.xream.x7.lock.customizer.LockProviderCustomizer;
import io.xream.x7.repository.jdbctemplate.JdbcTemplateHelper;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;


public class RepositoryListener implements
        SmartApplicationListener {

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == ApplicationStartedEvent.class;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE-1;
    }
    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        ApplicationStartedEvent applicationStartedEvent = (ApplicationStartedEvent)event;

        customizeLockProvider(applicationStartedEvent);

        if (!X7Data.isEnabled)
            return;

        customizeEnumSupport(applicationStartedEvent);

        customizeJsonConfig(applicationStartedEvent);

        customizeCacheStorage(applicationStartedEvent);

        customizeL2CacheConsistency(applicationStartedEvent);

        onJdbcHelperCreated(applicationStartedEvent);
        onStarted(applicationStartedEvent);

        idGenerator(applicationStartedEvent);
    }

    private void idGenerator(ApplicationStartedEvent applicationStartedEvent) {
        try {
            IdGenerator idGenerator = applicationStartedEvent.getApplicationContext()
                    .getBean(IdGenerator.class);
            DefaultRepositoryX defaultRepository = applicationStartedEvent.getApplicationContext()
                    .getBean(DefaultRepositoryX.class);
            if (defaultRepository != null && idGenerator != null) {
                defaultRepository.setIdGenerator(idGenerator);
            }
        }catch (Exception e) {

        }
    }

    private void customizeEnumSupport(ApplicationStartedEvent applicationStartedEvent) {

        EnumSupportCustomizer customizer = null;
        try {
            customizer = applicationStartedEvent.getApplicationContext().getBean(EnumSupportCustomizer.class);
        }catch (Exception e){

        }
        if (customizer == null || customizer.customize() == null) {
            EnumSupportListener.onStarted(new DefaultEnumSupport());
        }else {
            EnumSupportListener.onStarted(customizer.customize());
        }
    }

    private void customizeJsonConfig(ApplicationStartedEvent applicationStartedEvent){
        SqliJsonUtil.Customizer customizer = null;
        try{
            customizer = applicationStartedEvent.getApplicationContext().getBean(SqliJsonUtil.Customizer.class);
        }catch (Exception e){
        }
        if (customizer == null)
            return;
        ObjectMapper objectMapper = customizer.customize();
        if (objectMapper == null)
            return;
        customizer.onStarted(objectMapper);

    }

    private void onStarted(ApplicationStartedEvent applicationStartedEvent){
        Dialect dialect = null;
        try {
            dialect = applicationStartedEvent.getApplicationContext().getBean(Dialect.class);
        }catch (Exception e){

        }
        DialectCustomizer dialectCustomizer = null;
        try{
            dialectCustomizer = applicationStartedEvent.getApplicationContext().getBean(DialectCustomizer.class);
        }catch (Exception e) {}
        if (dialect == null && dialectCustomizer == null)
            return;
        SqliListener.customizeDialectOnStarted(dialect, dialectCustomizer);

        try {
            NativeRepository nativeRepository = applicationStartedEvent.getApplicationContext().getBean(NativeRepository.class);
            SqlInit sqlInit = applicationStartedEvent.getApplicationContext().getBean(SqlInit.class);
            Schema schema = applicationStartedEvent.getApplicationContext().getBean(Schema.class);
            TemporaryRepository temporaryRepository = applicationStartedEvent.getApplicationContext().getBean(TemporaryRepository.class);
            TemporaryTableParserListener.onStarted(temporaryRepository, schema);
            SqliListener.onStarted(nativeRepository, sqlInit, schema);
        }catch (Exception e) {

        }
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
        try {
            DistributionLock.init(lockProvider);
        }catch (Exception e) {

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
                if (jdbcHelper == null)
                    return false;
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

        if (cacheStorage == null)
            return;

        try {
            L2CacheResolver levelTwoCacheResolver = applicationStartedEvent.getApplicationContext().getBean(L2CacheResolver.class);
            if (levelTwoCacheResolver == null)
                return;
            levelTwoCacheResolver.setCacheStorage(cacheStorage);
        }catch (Exception e) {

        }

    }

    private void customizeL2CacheConsistency(ApplicationStartedEvent applicationStartedEvent) {
        L2CacheConsistencyCustomizer customizer = null;
        try {
            customizer = applicationStartedEvent.getApplicationContext().getBean(L2CacheConsistencyCustomizer.class);
        } catch (Exception e) {

        }

        if (customizer == null || customizer.customize() == null)
            return;

        try {
            L2CacheResolver l2CacheResolver = applicationStartedEvent.getApplicationContext().getBean(L2CacheResolver.class);

            if (l2CacheResolver == null)
                return;
            SqliListener.onL2CacheEnabled(l2CacheResolver, customizer.customize());
        }catch (Exception e) {

        }
    }


 
}
