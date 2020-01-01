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
package io.xream.x7.common.bean;

import io.xream.x7.common.repository.ReflectionCache;
import io.xream.x7.common.repository.X;
import io.xream.x7.common.util.BeanUtil;
import io.xream.x7.common.util.BeanUtilX;
import io.xream.x7.common.util.ExceptionUtil;
import io.xream.x7.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.xream.x7.common.bean.condition.InCondition;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Parser {

    private static Logger logger = LoggerFactory.getLogger(Parser.class);
    @SuppressWarnings("rawtypes")
    private final static Map<Class, Parsed> map = new ConcurrentHashMap<Class, Parsed>();

    private final static Map<String, Parsed> simpleNameMap = new ConcurrentHashMap<String, Parsed>();

    private final static Map<Class, ReflectionCache> cacheMap = new ConcurrentHashMap<Class, ReflectionCache>();

    public static String mappingPrefix;
    public static String mappingSpec;


    @SuppressWarnings("rawtypes")
    public static void put(Class clz, Parsed parsed) {
        map.put(clz, parsed);
        String key = BeanUtil.getByFirstLower(clz.getSimpleName());
        simpleNameMap.put(key, parsed);
    }

    @SuppressWarnings("rawtypes")
    public static Parsed get(Class clz) {
        Parsed parsed = map.get(clz);
        if (parsed == null) {
            parse(clz);
            parsed = map.get(clz);
            Field f = parsed.getKeyField(X.KEY_ONE);
            if (f == null)
                throw new RuntimeException("No Primary Key, class: " + clz.getName());
        }
        return parsed;
    }

    public static Parsed get(String simpleName) {
        return simpleNameMap.get(simpleName);
    }

    @SuppressWarnings({"rawtypes"})
    public static void parse(Class clz) {

        if (clz == Criteria.class || clz == Criteria.ResultMappedCriteria.class)
            throw new RuntimeException("parser unsupport Criteria, CriteriaJoinable, ....");

        List<BeanElement> elementList = BeanUtilX.getElementList(clz);
        Parsed parsed = new Parsed(clz);
        for (BeanElement element : elementList) {
            if (StringUtil.isNullOrEmpty(element.getMapper())) {
                element.initMaper();
            }
        }
        boolean isNoSpec = true;
        try {
            if (StringUtil.isNotNull(mappingSpec)) {
                isNoSpec = false;
            } else {
                for (BeanElement element : elementList) {
                    if (!element.getProperty().equals(element.getMapper())) {
                        isNoSpec = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        parsed.setNoSpec(isNoSpec);
        parsed.reset(elementList);
        BeanUtilX.parseKey(parsed, clz);

        /*
         * tableName,
         */
        X.Mapping mapping = (X.Mapping) clz.getAnnotation(X.Mapping.class);
        if (mapping != null) {
            String tableName = mapping.value();
            if (!tableName.equals("")) {
                parsed.setTableName(tableName);
                parsed.setOriginTable(tableName);
                parsed.setNoSpec(false);
            } else {
                String name = BeanUtil.getByFirstLower(clz.getSimpleName());
                String mapper = BeanUtil.getMapper(name);
                String prefix = mappingPrefix;
                if (StringUtil.isNotNull(prefix)) {
                    if (!prefix.endsWith("_")) {
                        prefix += "_";
                    }
                    mapper = prefix + mapper;
                }

                parsed.setTableName(mapper);
                parsed.setOriginTable(mapper);
            }
        } else {
            String name = BeanUtil.getByFirstLower(clz.getSimpleName());
            String mapper = BeanUtil.getMapper(name);
            String prefix = mappingPrefix;
            if (StringUtil.isNotNull(prefix)) {
                if (!prefix.endsWith("_")) {
                    prefix += "_";
                }
                mapper = prefix + mapper;
            }

            parsed.setTableName(mapper);
            parsed.setOriginTable(mapper);
        }

        /*
         * 排序
         */
        BeanElement one = null;
        Iterator<BeanElement> ite = elementList.iterator();
        while (ite.hasNext()) {
            BeanElement be = ite.next();
            if (be.getProperty().equals(parsed.getKey(X.KEY_ONE))) {
                one = be;
                ite.remove();
                continue;
            }
        }

        elementList.add(0, one);

        Iterator<BeanElement> beIte = parsed.getBeanElementList().iterator();
        while (beIte.hasNext()) {
            if (null == beIte.next()) {
                beIte.remove();
            }
        }

        /*
         * parseCacheable
         */
        BeanUtilX.parseCacheableAnno(clz, parsed);

        put(clz, parsed);

        /*
         * parse search
         */
        BeanUtilX.parseSearch(parsed, clz);
    }

    public static ReflectionCache getReflectionCache(Class clz) {
        ReflectionCache cache = cacheMap.get(clz);
        if (cache == null) {
            cache = new ReflectionCache();
            cache.setClz(clz);
            cache.cache();
            cacheMap.put(clz, cache);
        }
        return cache;
    }

    public static Parsed getByTableName(String tableName) {
        for (Parsed parsed : map.values()) {
            if (parsed.getOriginTable().equals(tableName))
                return parsed;
        }
        return null;
    }


    public static <T> T toLogic(Transformed transformed, Class<T> clz) {

        if (transformed == null)
            return null;

        T t = null;
        try {
            t = clz.newInstance();

            Parsed parsed = Parser.get(clz);
            Parsed parsedTransformed = get(transformed.getClass());

            List<BeanElement> logicBeanElementList = parsed.getBeanElementList();
            List<BeanElement> transformedBeanElementList = parsedTransformed.getBeanElementList();

            for (BeanElement logicBe : logicBeanElementList) {
                String logicMapper = logicBe.getMapper();
                for (BeanElement transformedBe : transformedBeanElementList) {
                    String transformedMapper = transformedBe.getMapper();
                    if (logicMapper.equals(transformedMapper)) { //找到了

                        Object propertyValue = transformedBe.getMethod.invoke(transformed); //logic对象的属性值
                        if (propertyValue == null)
                            continue;
                        logicBe.setMethod.invoke(t, propertyValue);//赋给存储对象的属性
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

        return t;
    }

    public static <T> Transformed transform(T logic) {

        Class clz = logic.getClass();

        Parsed parsed = Parser.get(clz);

        Parsed parsedTransformed = parsed.getParsedTransformed();

        if (parsedTransformed == null)
            throw new RuntimeException("SchemaTransform enabled, but can't find config for logic table to target");

        /*
         * TODO:
         * 接下来要完成的coding
         * 反射, 把逻辑对象值赋值给存储对象
         */

        Transformed transformed = null;
        try {
            Class clazz = parsedTransformed.getClz();
            transformed = (Transformed) clazz.newInstance();

            transformed.setAlia(parsed.getTransformedAlia());

            /*
             *  如何通过列名找到属性名
             */
            List<BeanElement> logicBeanElementList = parsed.getBeanElementList();
            List<BeanElement> transformedBeanElementList = parsedTransformed.getBeanElementList();


            for (BeanElement logicBe : logicBeanElementList) {
                String logicMapper = logicBe.getMapper();
                for (BeanElement transformedBe : transformedBeanElementList) {
                    String transformedMapper = transformedBe.getMapper();
                    if (logicMapper.equals(transformedMapper)) { //找到了

                        Object propertyValue = logicBe.getMethod.invoke(logic); //logic对象的属性值
                        if (propertyValue == null)
                            continue;
                        transformedBe.setMethod.invoke(transformed, propertyValue);//赋给存储对象的属性
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

        return transformed;
    }

    public static <T> Transformed transformForRemove(T logic) {

        Class clz = logic.getClass();

        Parsed parsed = Parser.get(clz);

        Parsed parsedTransformed = parsed.getParsedTransformed();

        if (parsedTransformed == null)
            throw new RuntimeException("SchemaTransform enabled, but can't find config for logic table to target");


        Transformed transformed = null;
        try {
            Class clazz = parsedTransformed.getClz();
            transformed = (Transformed) clazz.newInstance();

            Field logicKeyField = parsed.getKeyField(X.KEY_ONE);
            Object keyOne = logicKeyField.get(logic);

            Field transformedKeyField = parsedTransformed.getKeyField(X.KEY_ONE);
            transformedKeyField.set(transformed, keyOne); //delete by pk

        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

        return transformed;
    }

    public static <T> Class<? extends Transformed> transformClzz(Class<T> clz) {

        Parsed parsed = get(clz);
        Parsed parsedTransformed = parsed.getParsedTransformed();

        return parsedTransformed.getClz();
    }

    public static InCondition toTransformedInCondition(InCondition inCondition) {

        Class clz = inCondition.getClz();

        Parsed parsed = get(clz);
        Parsed parsedTransformed = parsed.getParsedTransformed();

        List<BeanElement> transformedBeanElementList = parsedTransformed.getBeanElementList();

        BeanElement logicPropertyBe = parsed.getElement(inCondition.getProperty());
        String mapper = logicPropertyBe.mapper;
        String propertyTransformed = logicPropertyBe.property;

        for (BeanElement transformedBe : transformedBeanElementList) {
            String transformedMapper = transformedBe.getMapper();
            if (mapper.equals(transformedMapper)) { //找到了
                propertyTransformed = transformedBe.property;
                break;
            }
        }


        InCondition inConditionTransformed = new InCondition();
        inConditionTransformed.setClz(parsedTransformed.getClz());
        inConditionTransformed.setProperty(propertyTransformed);
        inConditionTransformed.setInList(inCondition.getInList());

        return inConditionTransformed;
    }
}
