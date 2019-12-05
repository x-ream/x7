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
package x7.repository.transform;

import x7.core.bean.Criteria;
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.RefreshCondition;
import x7.core.web.Page;
import x7.repository.KeyOne;
import x7.repository.dao.Dao;

import java.util.List;
import java.util.Map;

public class SqlDataTransform implements DataTransform {

    private Dao dao;
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    @Deprecated
    @Override
    public <T> void refreshCache(Class<T> clz) {
        throw new RuntimeException("Wrong Code");
    }

    @Override
    public long create(Object obj) {

//        if (SchemaConfig.isNormal(obj.getClass()))
            return this.dao.create(obj);

//        Transformed transformed = Parser.transform(obj);
//
//        createId(obj, transformed);
//
//        return this.dao.create(transformed);
    }

    @Override
    public boolean createBatch(List<?> objList) {
//        Object first = objList.get(0);
//        if (SchemaConfig.isNormal(first.getClass()))
            return this.dao.createBatch(objList);

//        List<Transformed> list = new ArrayList<>();
//        for (Object obj : objList) {
//            Transformed tf = Parser.transform(obj);
//            createId(obj, tf);
//            list.add(tf);
//        }
//
//        return this.dao.createBatch(list);
    }


    @Override
    public <T> boolean refresh(RefreshCondition<T> refreshCondition) {

//        if (SchemaConfig.isNormal(refreshCondition.getClz()))
            return this.dao.refreshByCondition(refreshCondition);

//        RefreshCondition refreshConditionTransformed = new RefreshCondition();
//        refreshConditionTransformed.setClz(refreshCondition.getClz());
//        refreshConditionTransformed.setRefreshList(refreshCondition.getRefreshList());
//        refreshConditionTransformed.setCondition(refreshCondition.getCondition());
//        refreshConditionTransformed.setSourceStript(refreshCondition.getSourceStript());
//
//        Object obj = refreshCondition.getObj();
//        if (Objects.nonNull(obj)) {
//            Transformed transformed = Parser.transform(obj);
//            refreshConditionTransformed.setObj(transformed);
//        }
//
//        return this.dao.refreshByCondition(refreshConditionTransformed);
    }

    @Override
    public <T> boolean remove(KeyOne<T> keyOne) {
//        if (SchemaConfig.isNormal(obj.getClass()))
            return this.dao.remove(keyOne);

//        Transformed transformed = Parser.transformForRemove(obj);
//        return this.dao.remove(transformed);
    }


    @Override
    public <T> boolean execute(T obj, String sql) {
//        if (SchemaConfig.isNormal(obj.getClass()))
            return this.dao.execute(obj, sql);

//        Transformed transformed = Parser.transform(obj);
//        return this.dao.execute(transformed, sql);
    }


    @Override
    public <T> List<T> list(Object obj) {
//        if (SchemaConfig.isNormal(obj.getClass()))
            return this.dao.list(obj);

//        Transformed transformed = Parser.transform(obj);
//
//        List<Transformed> transformedList = this.dao.list(transformed);
//
//        Class<T> clzz = (Class<T>) obj.getClass();
//        List<T> list = new ArrayList<>();
//        for (Transformed tf : transformedList) {
//            T t = Parser.toLogic(tf, clzz);
//            list.add(t);
//        }
//
//        return list;
    }

    @Override
    public List<Map<String, Object>> list(Class clz, String sql, List<Object> conditionList) {
        return this.dao.list(clz, sql, conditionList);
    }

    @Override
    public <T> T getOne(T obj) {//带ID查询, 不需要alia; 不带ID查询,需要alia
//        if (SchemaConfig.isNormal(obj.getClass()))
            return this.dao.getOne(obj);

//        Transformed transformed = Parser.transform(obj);
//        Transformed tf = this.dao.getOne(transformed);
//        if (Objects.isNull(tf))
//            return null;
//        T t = Parser.toLogic(tf, (Class<T>) obj.getClass());
//        return t;
    }



    @Override
    public <T> List<T> in(InCondition inCondition) {
//        if (SchemaConfig.isNormal(inCondition.getClz()))
            return this.dao.in(inCondition);

//        InCondition inConditionTransformed = Parser.toTransformedInCondition(inCondition);

        // TODO: 如果 inProperty不是keyOne, 需要拼接上alia
//        List<Transformed> transformedList = this.dao.in(inConditionTransformed);
//
//        List<T> list = new ArrayList<>();
//        for (Transformed transformed : transformedList){
//            T logic = (T)Parser.toLogic(transformed, inCondition.getClz());
//            list.add(logic);
//        }
//
//        return list;
    }


    @Override
    public <T> Page<T> find(Criteria criteria) {

//        Class clzz = criteria.getClz();
//
//        if (SchemaConfig.isNormal(clzz))
            return this.dao.find(criteria);

//        Parsed parsed = Parser.get(clzz);
//        Criteria.X x = new Criteria.X();
//        x.setKey("alia");
//        x.setConjunction(Conjunction.AND);
//        x.setPredicate(Predicate.EQ);
//        x.setValue(parsed.getTransformedAlia());
//
//        Class clzzTransformed = Parser.transformClzz(clzz);
//
//        criteria.getListX().add(x);
//        criteria.setClz(clzzTransformed);
//
//        Page page = this.dao.find(criteria);
//
//        List list = new ArrayList();
//        List listTransformed = page.getList();
//        for (Object obj : listTransformed){
//            Object o = Parser.toLogic((Transformed) obj,clzz);
//            list.add(o);
//        }
//        page.setClz(clzz);
//        page.reSetList(list);
//
//        return page;
    }

    @Override
    public Page<Map<String, Object>> find(Criteria.ResultMappedCriteria criteria) {

        /**
         * TODO:
         */
        return this.dao.find(criteria);
    }

    @Override
    public List<Map<String, Object>> list(Criteria.ResultMappedCriteria criteria) {

        /**
         * TODO:
         */

        return this.dao.list(criteria);
    }

    @Override
    public <T> List<T> list(Criteria criteria) {
//        Class clzz = criteria.getClz();
//
//        if (SchemaConfig.isNormal(clzz))
            return this.dao.list(criteria);

//        Parsed parsed = Parser.get(clzz);
//        Criteria.X x = new Criteria.X();
//        x.setKey("alia");
//        x.setConjunction(Conjunction.AND);
//        x.setPredicate(Predicate.EQ);
//        x.setValue(parsed.getTransformedAlia());
//
//        Class clzzTransformed = Parser.transformClzz(clzz);
//
//        criteria.getListX().add(x);
//        criteria.setClz(clzzTransformed);
//
//        return this.dao.list(criteria);
    }


//    private void createId(Object obj, Transformed transformed) {
//        Parsed transformedParsed = Parser.get(transformed.getClass());
//        Field field = transformedParsed.getKeyField(X.KEY_ONE);
//        try {
//            Object keyOneObj = field.get(transformed);
//            if (Objects.nonNull(keyOneObj))
//                return;
//        }catch (Exception e){
//            throw new RuntimeException(ExceptionUtil.getMessage(e));
//        }
//        String keyOne = null;
//        if (field.getType() == String.class) {
//
//            Parsed parsed = Parser.get(obj.getClass());
//            Field f = parsed.getKeyField(X.KEY_ONE);
//
//            if (f.getType() == String.class) {
//                try {
//                    keyOne = (String) f.get(obj);
//                } catch (Exception e) {
//                    throw new RuntimeException(ExceptionUtil.getMessage(e));
//                }
//            } else {
//                UUID uuid = UUID.randomUUID();
//                keyOne = uuid.toString().replace("-", "").toUpperCase();
//            }
//
//            try {
//                field.set(transformed, keyOne.toUpperCase());
//            } catch (Exception e) {
//                throw new RuntimeException(ExceptionUtil.getMessage(e));
//            }
//
//        } else {
//            throw new RuntimeException("Transform target table primary key must be String");
//        }
//    }
}
