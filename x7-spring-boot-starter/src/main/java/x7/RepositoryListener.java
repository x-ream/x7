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
package x7;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import x7.core.bean.BeanElement;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.bean.TransformConfigurable;
import x7.core.repository.CacheResolver;
import x7.repository.BaseRepository;
import x7.repository.CacheableRepository;
import x7.repository.Repository;
import x7.repository.RepositoryBootListener;
import x7.repository.cache.*;
import x7.repository.cache.customizer.L2CacheStoragePolicyCustomizer;
import x7.repository.cache.customizer.L3CacheArgsToStringCustomizer;
import x7.repository.cache.customizer.L3CacheStoragePolicyCustomizer;
import x7.repository.id.IdGeneratorPolicy;
import x7.repository.id.customizer.IdGeneratorPolicyCustomizer;
import x7.repository.mapper.MapperFactory;
import x7.repository.schema.SchemaConfig;
import x7.repository.schema.SchemaTransformRepository;
import x7.repository.schema.customizer.SchemaTransformCustomizer;
import x7.repository.schema.customizer.SchemaTransformRepositoryBuilder;
import x7.repository.transform.DataTransform;
import x7.repository.transform.customizer.DataTransformCustomizer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RepositoryListener implements
        ApplicationListener<ApplicationStartedEvent> {


    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {


        customizeL3CacheArgsToString(applicationStartedEvent);
        customizeL3CacheStoragePolicy(applicationStartedEvent);

        if (!X7Data.isEnabled)
            return;

        customizeCacheStoragePolicy(applicationStartedEvent);

        customizeIdGeneratorPolicy(applicationStartedEvent);

        customizeDataTransform(applicationStartedEvent);

        RepositoryBootListener.onStarted(applicationStartedEvent.getApplicationContext());

        transform(applicationStartedEvent);
    }

    private void customizeL3CacheStoragePolicy(ApplicationStartedEvent applicationStartedEvent) {

        try {
            L3CacheAspect bean = applicationStartedEvent.getApplicationContext().getBean(L3CacheAspect.class);
            if (bean == null)
                return;

            L3CacheStoragePolicyCustomizer customizer = null;
            try {
                customizer = applicationStartedEvent.getApplicationContext().getBean(L3CacheStoragePolicyCustomizer.class);
            } catch (Exception e) {
            }

            final L3CacheStoragePolicy storagePolicy;
            if (customizer == null) {
                storagePolicy = new DefaultL3CacheStoragePolicy();
                StringRedisTemplate stringRedisTemplate = applicationStartedEvent.getApplicationContext().getBean(StringRedisTemplate.class);
                ((DefaultL3CacheStoragePolicy) storagePolicy).setStringRedisTemplate(stringRedisTemplate);
            } else {
                storagePolicy = customizer.customize();
            }

            L3CacheResolver resolver = () -> storagePolicy;

            bean.setResolver(resolver);

        } catch (Exception e) {

        }
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
            if (customizer == null) {
                argsToString = new DefaultArgsToString();
            } else {
                argsToString = customizer.customize();
            }
            bean.setArgsToString(argsToString);
        } catch (Exception e) {

        }

    }


    private void customizeDataTransform(ApplicationStartedEvent applicationStartedEvent) {
        DataTransformCustomizer customizer = null;
        try {
            customizer = applicationStartedEvent.getApplicationContext().getBean(DataTransformCustomizer.class);
        } catch (Exception e) {

        }

        if (customizer == null)
            return;

        DataTransform dataTransform = customizer.customize();
        if (dataTransform == null)
            return;

        Repository repository = applicationStartedEvent.getApplicationContext().getBean(Repository.class);
        if (repository == null)
            return;
        ((CacheableRepository) repository).setDataTransform(dataTransform);
    }

    private void customizeCacheStoragePolicy(ApplicationStartedEvent applicationStartedEvent) {

        L2CacheStoragePolicyCustomizer customizer = null;
        try {
            customizer = applicationStartedEvent.getApplicationContext().getBean(L2CacheStoragePolicyCustomizer.class);
        } catch (Exception e) {

        }

        if (customizer == null)
            return;

        L2CacheStoragePolicy cacheStoragePolicy = customizer.customize();
        if (cacheStoragePolicy == null)
            return;

        CacheResolver levelTwoCacheResolver = applicationStartedEvent.getApplicationContext().getBean(CacheResolver.class);
        if (levelTwoCacheResolver == null)
            return;
        ((DefaultL2CacheResolver) levelTwoCacheResolver).setCacheStoragePolicy(cacheStoragePolicy);

    }


    private void customizeIdGeneratorPolicy(ApplicationStartedEvent applicationStartedEvent) {
        IdGeneratorPolicyCustomizer customizer = null;
        try {
            customizer = applicationStartedEvent.getApplicationContext().getBean(IdGeneratorPolicyCustomizer.class);
        } catch (Exception e) {

        }

        if (customizer == null)
            return;

        IdGeneratorPolicy idGeneratorPolicy = customizer.customize();
        if (idGeneratorPolicy == null)
            return;

        Repository.IdGenerator idGenerator = applicationStartedEvent.getApplicationContext().getBean(Repository.IdGenerator.class);
        if (idGenerator == null)
            return;
        idGenerator.setIdGeneratorPolicy(idGeneratorPolicy);

    }


    private void transform(ApplicationStartedEvent applicationStartedEvent) {
        List<Class<? extends BaseRepository>> clzzList = null;
        if (SchemaConfig.isSchemaTransformEnabled) {
            clzzList = customizeSchemaTransform(applicationStartedEvent);
        }

        if (clzzList != null) {

            for (Class<? extends BaseRepository> clzz : clzzList) {

                Repository depository = applicationStartedEvent.getApplicationContext().getBean(Repository.class);

                List list = list(depository, clzz);//查出所有配置
                if (!list.isEmpty()) {
                    reparse(list);
                }
            }
        }
    }


    private List<Class<? extends BaseRepository>> customizeSchemaTransform(ApplicationStartedEvent applicationStartedEvent) {


        SchemaTransformCustomizer customizer = null;
        try {
            customizer = applicationStartedEvent.getApplicationContext().getBean(SchemaTransformCustomizer.class);
        } catch (Exception e) {
        }

        if (customizer != null) {
            SchemaTransformRepositoryBuilder builder = new SchemaTransformRepositoryBuilder();
            return customizer.customize(builder);
        }

        SchemaTransformRepositoryBuilder.registry = null;

        List<Class<? extends BaseRepository>> list = new ArrayList<>();
        list.add(SchemaTransformRepository.class);
        return list;
    }


    private void reparse(List list) {

        //key: originTable
        Map<String, List<TransformConfigurable>> map = new HashMap<>();

        for (Object obj : list) {
            if (obj instanceof TransformConfigurable) {

                TransformConfigurable transformed = (TransformConfigurable) obj;
                String originTable = transformed.getOriginTable();
                List<TransformConfigurable> transformedList = map.get(originTable);
                if (transformedList == null) {
                    transformedList = new ArrayList<>();
                    map.put(originTable, transformedList);
                }
                transformedList.add(transformed);
            }
        }

        for (Map.Entry<String, List<TransformConfigurable>> entry : map.entrySet()) {
            String originTable = entry.getKey();

            Parsed parsed = Parser.getByTableName(originTable);
            if (parsed == null)
                continue;

            List<TransformConfigurable> transformedList = entry.getValue();
            for (TransformConfigurable transformed : transformedList) {
                parsed.setTableName(transformed.getTargetTable());//FIXME 直接替换了原始的表
                parsed.setTransforemedAlia(transformed.getAlia());

                for (BeanElement be : parsed.getBeanElementList()) {
                    if (be.getMapper().equals(transformed.getOriginColumn())) {
                        be.mapper = transformed.getTargetColumn();//FIXME 直接替换了原始的列, 只需要目标对象的属性有值
                        break;
                    }
                }
            }

            parsed.reset(parsed.getBeanElementList());
            String tableName = parsed.getTableName();
            Parsed parsedTransformed = Parser.getByTableName(tableName);
            parsed.setParsedTransformed(parsedTransformed);

            SchemaConfig.transformableSet.add(parsed.getClz());

            Map<String, String> sqlMap = MapperFactory.getSqlMap(parsedTransformed.getClz());
            MapperFactory.putSqlMap(parsed.getClz(), sqlMap);
        }
    }

    private List list(Repository dataRepository, Class<? extends BaseRepository> clzz) {

        Type[] types = clzz.getGenericInterfaces();

        ParameterizedType parameterized = (ParameterizedType) types[0];
        Class clazz = (Class) parameterized.getActualTypeArguments()[0];

        Object obj = null;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        List list = dataRepository.list(obj);

        return list;
    }

}
