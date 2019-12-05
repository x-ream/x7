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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x7.core.bean.*;
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.RefreshCondition;
import x7.core.repository.CacheResolver;
import x7.core.repository.X;
import x7.core.util.BeanUtilX;
import x7.core.util.JsonX;
import x7.core.web.Page;
import x7.repository.transform.DataTransform;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Sim
 */
public class CacheableRepository implements Repository {

    private final static Logger logger = LoggerFactory.getLogger(Repository.class);

    public CacheableRepository() {
        ManuRepository.init(this);
    }

    private DataTransform dataTransform;
    public void setDataTransform(DataTransform dataTransform) {

        logger.info("X7 Repository on starting....");

        this.dataTransform = dataTransform;
    }

    private CacheResolver cacheResolver;

    public void setCacheResolver(CacheResolver cacheResolver) {
        this.cacheResolver = cacheResolver;
    }

    private boolean isNoCache() {
        return !cacheResolver.isEnabled();
    }

    private String getCacheKey(Object obj, Parsed parsed) {
        try {

            Field field = obj.getClass().getDeclaredField(parsed.getKey(X.KEY_ONE));
            field.setAccessible(true);
            String keyOne = field.get(obj).toString();
            return keyOne;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T> void replenishAndRefreshCache(List<String> keyList, List<T> list, Class<T> clz, Parsed parsed) {

        Set<String> keySet = new HashSet<String>();
        for (T t : list) {
            String key = getCacheKey(t, parsed);
            keySet.add(key);
        }

        for (String key : keyList) {
            if (!keySet.contains(key)) {

                T obj = null;

                Field f = parsed.getKeyField(X.KEY_ONE);

                T condition = null;
                try {
                    condition = clz.newInstance();
                    f.set(condition, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                obj = dataTransform.getOne(condition);


                /*
                 * 更新或重置缓存
                 */
                if (obj == null) {
                    if (!isNoCache() && !parsed.isNoCache())
                        cacheResolver.markForRefresh(clz);
                } else {
                    list.add(obj);
                    if (!isNoCache() && !parsed.isNoCache())
                        cacheResolver.set(clz, key, obj);
                }
            }
        }

    }

    private <T> List<T> sort(List<String> keyList, List<T> list, Parsed parsed) {
        List<T> sortedList = new ArrayList<T>();
        for (String key : keyList) {
            Iterator<T> ite = list.iterator();
            while (ite.hasNext()) {
                T t = ite.next();
                if (key.equals(getCacheKey(t, parsed))) {
                    ite.remove();
                    sortedList.add(t);
                    break;
                }
            }
        }
        return sortedList;
    }


    @Override
    public long create(Object obj) {

        Class clz = obj.getClass();
        Parsed parsed = Parser.get(clz);
        long id = dataTransform.create(obj);

        if (!isNoCache() && !parsed.isNoCache())
            cacheResolver.markForRefresh(clz);
        return id;
    }


    @Override
    public <T> boolean refresh(RefreshCondition<T> refreshCondition) {

        boolean flag = false;

        CriteriaCondition condition = refreshCondition.getCondition();
        Class clz = refreshCondition.getClz();
        if (condition instanceof Criteria) {
            Criteria criteria = (Criteria) condition;
            criteria.setClz(refreshCondition.getClz());
        }
        Parsed parsed = Parser.get(clz);

        flag = dataTransform.refresh(refreshCondition);

        if (!isNoCache() && !parsed.isNoCache()) {


            cacheResolver.remove(clz);
            cacheResolver.markForRefresh(clz);

            String keyOne = parsed.getKey(X.KEY_ONE);
            Object pk = null;
            for (Criteria.X x : refreshCondition.getCondition().getListX()) {
                String key = x.getKey();
                if (key != null && key.equals(keyOne)) {
                    pk = x.getValue();
                }
            }

            if (pk != null)
                cacheResolver.remove(clz, String.valueOf(pk));

        }
        return flag;
    }

    /**
     * 配合refreshTime使用，后台按更新时间查询列表之前调用
     *
     * @param clz
     */
    public <T> void refreshCache(Class<T> clz) {
        Parsed parsed = Parser.get(clz);
        if (!isNoCache() && !parsed.isNoCache()) {
            cacheResolver.markForRefresh(clz);
        }
    }

    @Override
    public <T> boolean remove(KeyOne<T> keyOne) {

        boolean flag = false;
        Class clz = keyOne.getClzz();
        Parsed parsed = Parser.get(clz);
        String key = String.valueOf(keyOne.get());
        flag = dataTransform.remove(keyOne);

        if (!isNoCache() && !parsed.isNoCache()) {
            if (key != null)
                cacheResolver.remove(clz, key);
            cacheResolver.markForRefresh(clz);
        }
        return flag;
    }


    @Override
    public <T> List<T> list(Object conditionObj) {

        if (conditionObj instanceof CriteriaBuilder || conditionObj instanceof Criteria)
            throw new RuntimeException("Notes: parameter is not Criteria");

        Class clz = conditionObj.getClass();
        Parsed parsed = Parser.get(clz);

        if (isNoCache() || parsed.isNoCache()) {
            return dataTransform.list(conditionObj);
        }

        List<T> list = null;

        List<String> keyList = cacheResolver.getResultKeyList(clz, conditionObj);

        if (keyList == null || keyList.isEmpty()) {
            list = dataTransform.list(conditionObj);

            keyList = new ArrayList<String>();

            for (T t : list) {
                String key = getCacheKey(t, parsed);
                keyList.add(key);
            }

            cacheResolver.setResultKeyList(clz, conditionObj, keyList);

            return list;
        }

        list = cacheResolver.list(clz, keyList);

        if (keyList.size() == list.size())
            return list;

        replenishAndRefreshCache(keyList, list, clz, parsed);

        List<T> sortedList = sort(keyList, list, parsed);

        return sortedList;
    }

    @Override
    public <T> T getOne(T conditionObj) {

        Class<T> clz = (Class<T>) conditionObj.getClass();
        Parsed parsed = Parser.get(clz);

        if (isNoCache() || parsed.isNoCache()) {
            T t = dataTransform.getOne(conditionObj);
            return t;
        }

        String condition = JsonX.toJson(conditionObj);
        T obj = cacheResolver.get(clz, condition);

        if (obj == null) {
            obj = dataTransform.getOne(conditionObj);
            cacheResolver.set(clz, condition, obj);
            return obj;
        }

        return obj;
    }


    @Override
    public <T> Page<T> find(Criteria criteria) {

        Class clz = criteria.getClz();
        Parsed parsed = Parser.get(clz);


        if (isNoCache()) {
            return dataTransform.find(criteria);
        }

        List<T> list = null;

        Page<T> p = cacheResolver.getResultKeyListPaginated(clz, criteria);// FIXME

        if (p == null) {
            dataTransform.find(criteria);

            list = p.getList(); // 结果

            List<String> keyList = p.getKeyList();

            for (T t : list) {

                String key = getCacheKey(t, parsed);
                keyList.add(key);
            }

            p.reSetList(null);

            cacheResolver.setResultKeyListPaginated(clz, criteria, p);

            p.setKeyList(null);
            p.reSetList(list);

            return p;
        }

        List<String> keyList = p.getKeyList();

        if (keyList == null || keyList.isEmpty()) {
            return p;
        }

        list = cacheResolver.list(clz, keyList);

        if (keyList.size() == list.size()) {
            p.reSetList(list);
            return p;
        }

        replenishAndRefreshCache(keyList, list, clz, parsed);

        List<T> sortedList = sort(keyList, list, parsed);

        p.reSetList(sortedList);

        return p;
    }

    @Override
    public <T> List<T> list(Criteria criteria) {

        Class clz = criteria.getClz();
        Parsed parsed = Parser.get(clz);

        if (isNoCache()) {
            return dataTransform.list(criteria);
        }

        List<T> list = null;

        List<String> keyList = cacheResolver.getResultKeyList(clz, criteria);

        if (keyList == null || keyList.isEmpty()) {
            list = dataTransform.list(criteria);

            keyList = new ArrayList<>();

            for (T t : list) {
                String key = getCacheKey(t, parsed);
                keyList.add(key);
            }

            cacheResolver.setResultKeyList(clz, criteria, keyList);

            return list;
        }

        list = cacheResolver.list(clz, keyList);

        if (keyList.size() == list.size())
            return list;

        replenishAndRefreshCache(keyList, list, clz, parsed);

        List<T> sortedList = sort(keyList, list, parsed);

        return sortedList;

    }


    protected <T> boolean execute(T obj, String sql) {

        boolean b;
        Parsed parsed = Parser.get(obj.getClass());
        b = dataTransform.execute(obj, sql);

        if (b) {
            String key = getCacheKey(obj, parsed);
            if (!isNoCache() && !parsed.isNoCache()) {
                if (key != null) {
                    cacheResolver.remove(obj.getClass(), key);
                }
            }
        }

        return b;
    }


    protected <T> List<T> in0(InCondition inCondition) {

        Class clz = inCondition.getClz();
        String inProperty = inCondition.getProperty();

        Parsed parsed = Parser.get(clz);

        List<? extends Object> inList = inCondition.getInList();

        if (isNoCache() || parsed.isNoCache()) {
            return dataTransform.in(inCondition);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(inProperty).append(":");
        for (Object obj : inList) {
            sb.append(obj.toString()).append("_");
        }
        String condition = sb.toString();

        List<String> keyList = cacheResolver.getResultKeyList(clz, condition);

        List<T> list = null;

        if (keyList == null || keyList.isEmpty()) {

            list = dataTransform.in(inCondition);

            keyList = new ArrayList<String>();

            for (T t : list) {
                String key = getCacheKey(t, parsed);
                keyList.add(key);
            }

            cacheResolver.setResultKeyList(clz, condition, keyList);

            return list;
        }

        list = cacheResolver.list(clz, keyList);// FIXME 可能要先转Object

        if (keyList.size() == list.size())
            return list;

        replenishAndRefreshCache(keyList, list, clz, parsed);

        List<T> sortedList = sort(keyList, list, parsed);

        return sortedList;

    }


    @Override
    public <T> List<T> in(InCondition inCondition) {

        Parsed parsed = Parser.get(inCondition.getClz());
        if (inCondition.getInList().isEmpty())
            return new ArrayList<T>();

        List<Object> inList = new ArrayList<Object>();

        for (Object obj : inCondition.getInList()) {
            if (Objects.isNull(obj))
                continue;
            if (BeanUtilX.isBaseType_0(inCondition.getProperty(),obj,parsed))
                continue;
            if (!inList.contains(obj)) {
                inList.add(obj);
            }
        }

        if (inList.isEmpty())
            return new ArrayList<T>();

        int size = inList.size();

        if (size <= IN_MAX) {
            inCondition.setInList(inList);
            return in0(inCondition);
        }

        List<T> list = new ArrayList<>(size);
        int i = 0;
        while (size > 0) {
            int segSize = (size > IN_MAX ? IN_MAX : size);
            size -= segSize;
            int fromIndex = i++ * IN_MAX;
            int toIndex = fromIndex + segSize;
            List<? extends Object> segInList = inList.subList(fromIndex, toIndex);

            InCondition ic = new InCondition(inCondition.getProperty(), segInList);
            ic.setClz(inCondition.getClz());
            List<T> segList = in0(ic);
            list.addAll(segList);
        }

        return list;
    }


    @Override
    public Page<Map<String, Object>> find(Criteria.ResultMappedCriteria resultMapped) {
        return dataTransform.find(resultMapped);
    }

    @Override
    public List<Map<String, Object>> list(Criteria.ResultMappedCriteria resultMapped) {

        return dataTransform.list(resultMapped);
    }

    @Override
    public boolean createBatch(List<? extends Object> objList) {
        if (objList.isEmpty())
            return false;

        Class clz = objList.get(0).getClass();
        Parsed parsed = Parser.get(clz);
        boolean flag = this.dataTransform.createBatch(objList);
        if (!isNoCache() && !parsed.isNoCache())
            cacheResolver.markForRefresh(clz);

        return flag;
    }

    protected List<Map<String, Object>> list(Class clz, String sql, List<Object> conditionList) {

        Parsed parsed = Parser.get(clz);
        if (isNoCache() || parsed.isNoCache()) {
            return dataTransform.list(clz, sql, conditionList);
        }

        String condition = sql + conditionList.toString();

        List<Map<String, Object>> mapList = cacheResolver.getMapList(clz, condition);

        if (mapList == null) {
            mapList = dataTransform.list(clz, sql, conditionList);

            if (mapList != null) {
                cacheResolver.setMapList(clz, condition, mapList);
            }
        }

        return mapList;

    }




}
