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

import io.xream.x7.common.util.BeanMapUtil;
import io.xream.x7.common.util.BeanUtilX;
import io.xream.x7.common.util.StringUtil;
import io.xream.x7.common.bean.Criteria.ResultMappedCriteria;
import io.xream.x7.common.bean.Criteria.X;
import io.xream.x7.common.web.Direction;
import io.xream.x7.common.web.Fetched;
import io.xream.x7.common.web.MapResult;
import io.xream.x7.common.web.Paged;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Sim
 */
public class CriteriaBuilder {

    private Criteria criteria;
    private CriteriaBuilder instance;


    public PageBuilder paged() {
        return this.pageBuilder;
    }

    public void paged(Paged paged) {
        criteria.paged(paged);
        DataPermission.Chain.onBuild(criteria, paged);
    }

    public ConditionBuilder and() {

        X x = new X();
        x.setConjunction(Conjunction.AND);
        x.setValue(Conjunction.AND);

        X current = conditionBuilder.getX();
        if (current != null) {
            X parent = current.getParent();
            if (parent != null) {
                List<X> subList = parent.getSubList();
                if (subList != null) {
                    subList.add(x);
                    x.setParent(parent);
                }
            } else {
                this.criteria.add(x);
            }
        } else {
            this.criteria.add(x);
        }

        conditionBuilder.under(x);

        return conditionBuilder;
    }

    public ConditionBuilder or() {

        X x = new X();
        x.setConjunction(Conjunction.OR);
        x.setValue(Conjunction.OR);

        X current = conditionBuilder.getX();
        if (current != null) {
            X parent = current.getParent();
            if (parent != null) {
                List<X> subList = parent.getSubList();
                if (subList != null) {
                    subList.add(x);
                    x.setParent(parent);
                }
            } else {
                this.criteria.add(x);
            }
        } else {
            this.criteria.add(x);
        }

        conditionBuilder.under(x);

        return conditionBuilder;
    }

    public CriteriaBuilder endSub() {

        X x = new X();
        x.setPredicate(Predicate.SUB_END);
        x.setValue(Predicate.SUB_END);

        X current = conditionBuilder.getX();
        X parent = current.getParent();
        if (parent != null) {
            List<X> subList = parent.getSubList();
            if (subList != null) {
                subList.add(x);
            }

            this.conditionBuilder.under(parent);
        }

        return instance;
    }

    private PageBuilder pageBuilder = new PageBuilder() {

        @Override
        public PageBuilder scroll(boolean isScroll) {
            criteria.setScroll(isScroll);
            return this;
        }

        @Override
        public PageBuilder rows(int rows) {
            criteria.setRows(rows);
            return this;
        }

        @Override
        public PageBuilder page(int page) {
            criteria.setPage(page);
            return this;
        }

        @Override
        public PageBuilder orderIn(String porperty, List<? extends Object> inList) {
            if (Objects.nonNull(inList) && inList.size() > 0) {
                KV kv = new KV(porperty, inList);
                criteria.getFixedSortList().add(kv);
            }
            return this;
        }


        @Override
        public PageBuilder sort(String orderBy, Direction direction) {
            if (StringUtil.isNullOrEmpty(orderBy))
                return this;
            List<Sort> sortList = criteria.getSortList();
            if (sortList == null){
                sortList = new ArrayList<>();
                criteria.setSortList(sortList);
            }
            Sort sort = new Sort(orderBy,direction);
            sortList.add(sort);
            return this;
        }

        @Override
        public PageBuilder sort(String orderBy, Direction direction, List<Object> optValueList) {
            if (StringUtil.isNullOrEmpty(orderBy))
                return this;
            List<Sort> sortList = criteria.getSortList();
            if (sortList == null){
                sortList = new ArrayList<>();
                criteria.setSortList(sortList);
            }
            Sort sort = new Sort(orderBy,direction);
            sortList.add(sort);
            return this;
        }
    };

    private ConditionBuilder conditionBuilder = new ConditionBuilder() {

        private X x = null;

        @Override
        public X getX() {
            return x;
        }

        @Override
        public void under(X x) {
            this.x = x;
        }

        @Override
        public CriteriaBuilder eq(String property, Object value) {

            if (value == null)
                return instance;
            if (Objects.nonNull(criteria.getParsed())) {
                if (BeanUtilX.isBaseType_0(property, value,criteria.getParsed()))
                    return instance;
            }
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.EQ);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder lt(String property, Object value) {

            if (value == null)
                return instance;
            if (Objects.nonNull(criteria.getParsed())) {
                if (BeanUtilX.isBaseType_0(property, value,criteria.getParsed()))
                    return instance;
            }
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.LT);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder lte(String property, Object value) {

            if (value == null)
                return instance;
            if (Objects.nonNull(criteria.getParsed())) {
                if (BeanUtilX.isBaseType_0(property, value,criteria.getParsed()))
                    return instance;
            }
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.LTE);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder gt(String property, Object value) {

            if (value == null)
                return instance;
            if (Objects.nonNull(criteria.getParsed())) {
                if (BeanUtilX.isBaseType_0(property, value,criteria.getParsed()))
                    return instance;
            }
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.GT);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder gte(String property, Object value) {

            if (value == null)
                return instance;
            if (Objects.nonNull(criteria.getParsed())) {
                if (BeanUtilX.isBaseType_0(property, value,criteria.getParsed()))
                    return instance;
            }
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.GTE);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder ne(String property, Object value) {

            if (value == null)
                return instance;
            if (Objects.nonNull(criteria.getParsed())) {
                if (BeanUtilX.isBaseType_0(property, value,criteria.getParsed()))
                    return instance;
            }
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.NE);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder like(String property, String value) {

            if (StringUtil.isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.LIKE);
            x.setKey(property);
            x.setValue(SqlScript.LIKE_HOLDER + value + SqlScript.LIKE_HOLDER);

            return instance;
        }

        @Override
        public CriteriaBuilder likeRight(String property, String value) {

            if (StringUtil.isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.LIKE);
            x.setKey(property);
            x.setValue(value + SqlScript.LIKE_HOLDER);

            return instance;
        }

        @Override
        public CriteriaBuilder notLike(String property, String value) {

            if (StringUtil.isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.NOT_LIKE);
            x.setKey(property);
            x.setValue(SqlScript.LIKE_HOLDER + value + SqlScript.LIKE_HOLDER);

            return instance;
        }

        @Override
        public CriteriaBuilder between(String property, Object min, Object max) {

            if (min == null || max == null)
                return instance;
            if (Objects.nonNull(criteria.getParsed())) {
                if (BeanUtilX.isBaseType_0(property, max,criteria.getParsed()))
                    return instance;
            }
            if (isNullOrEmpty(max))
                return instance;
            if (isNullOrEmpty(min))
                return instance;

            MinMax minMax = new MinMax();
            minMax.setMin(min);
            minMax.setMax(max);

            x.setPredicate(Predicate.BETWEEN);
            x.setKey(property);
            x.setValue(minMax);

            return instance;
        }

        @Override
        public CriteriaBuilder in(String property, List<? extends Object> list) {

            if (list == null || list.isEmpty())
                return instance;

            List<Object> tempList = new ArrayList<Object>();
            for (Object obj : list) {
                if (Objects.isNull(obj))
                    continue;
                if (Objects.nonNull(criteria.getParsed())) {
                    if (BeanUtilX.isBaseType_0(property, obj,criteria.getParsed()))
                        continue;
                }
                if (!tempList.contains(obj)) {
                    tempList.add(obj);
                }
            }

            if (tempList.isEmpty())
                return instance;

            if (tempList.size() == 1) {
                return eq(property, tempList.get(0));
            }

            x.setPredicate(Predicate.IN);
            x.setKey(property);
            x.setValue(tempList);

            return instance;
        }

        @Override
        public CriteriaBuilder nin(String property, List<Object> list) {

            if (list == null || list.isEmpty())
                return instance;

            List<Object> tempList = new ArrayList<Object>();
            for (Object obj : list) {
                if (Objects.isNull(obj))
                    continue;
                if (Objects.nonNull(criteria.getParsed())) {
                    if (BeanUtilX.isBaseType_0(property, obj,criteria.getParsed()))
                        continue;
                }
                if (!tempList.contains(obj)) {
                    tempList.add(obj);
                }
            }

            if (tempList.isEmpty())
                return instance;

            if (tempList.size() == 1) {
                return ne(property, tempList.get(0));
            }

            x.setPredicate(Predicate.NOT_IN);
            x.setKey(property);
            x.setValue(tempList);

            return instance;
        }

        @Override
        public CriteriaBuilder nonNull(String property) {

            if (StringUtil.isNullOrEmpty(property))
                return instance;

            x.setPredicate(Predicate.IS_NOT_NULL);
            x.setValue(property);

            return instance;
        }

        @Override
        public CriteriaBuilder isNull(String property) {

            if (StringUtil.isNullOrEmpty(property))
                return instance;

            x.setPredicate(Predicate.IS_NULL);
            x.setValue(property);

            return instance;
        }

        @Override
        public CriteriaBuilder x(String sql) {

            if (StringUtil.isNullOrEmpty(sql))
                return instance;

            sql = BeanUtilX.normalizeSql(sql);

            x.setPredicate(Predicate.X);
            x.setKey(sql);
            x.setValue(null);

            return instance;
        }

        @Override
        public CriteriaBuilder x(String sql, List<Object> valueList) {

            if (StringUtil.isNullOrEmpty(sql))
                return instance;

            sql = BeanUtilX.normalizeSql(sql);

            x.setPredicate(Predicate.X);
            x.setKey(sql);
            x.setValue(valueList);

            return instance;
        }

        @Override
        public ConditionBuilder beginSub() {

            x.setKey(Predicate.SUB.sql());// special treat FIXME
            x.setValue(Predicate.SUB);

            List<X> subList = new ArrayList<>();
            x.setSubList(subList);

            X from = new X();
            from.setPredicate(Predicate.SUB_BEGIN);
            from.setValue(Predicate.SUB_BEGIN);

            subList.add(from);

            X xx = new X();//?
            subList.add(xx);//?
            xx.setParent(x);
            conditionBuilder.under(xx);

            return conditionBuilder;
        }

    };

    private CriteriaBuilder() {
        this.instance = this;
    }

    private CriteriaBuilder(Criteria criteria) {
        this.criteria = criteria;
        this.instance = this;
    }

    public static CriteriaBuilder buildCondition() {
        Criteria criteria = new Criteria();
        CriteriaBuilder builder = new CriteriaBuilder(criteria);
        return builder;
    }

    public static CriteriaBuilder build(Class<?> clz) {
        Criteria criteria = new Criteria();
        criteria.setClz(clz);
        CriteriaBuilder builder = new CriteriaBuilder(criteria);

        if (criteria.getParsed() == null) {
            Parsed parsed = Parser.get(clz);
            criteria.setParsed(parsed);
        }

        return builder;
    }

    public static CriteriaBuilder build(Class<?> clz, Paged paged) {
        Criteria criteria = new Criteria();
        criteria.setClz(clz);
        CriteriaBuilder builder = new CriteriaBuilder(criteria);

        if (criteria.getParsed() == null) {
            Parsed parsed = Parser.get(clz);
            criteria.setParsed(parsed);
        }

        if (paged != null) {
            builder.paged(paged);
        }

        return builder;
    }


    public static ResultMappedBuilder buildResultMapped(Class<?> clz, Fetched ro) {
        CriteriaBuilder b = new CriteriaBuilder();
        ResultMappedBuilder builder = b.new ResultMappedBuilder(clz);

        if (ro != null) {

            builder.xAddResultKey(ro);

            if (ro instanceof Paged) {
                builder.paged((Paged) ro);
            }

        }

        return builder;
    }

    public static ResultMappedBuilder buildResultMapped(Class<?> clz, MapResult ro) {
        CriteriaBuilder b = new CriteriaBuilder();
        ResultMappedBuilder builder = b.new ResultMappedBuilder(clz);

        if (ro != null) {

            builder.xAddResultKey(ro);

            if (ro instanceof Paged) {
                builder.paged((Paged) ro);
            }

        }

        return builder;
    }

    public static ResultMappedBuilder buildResultMapped(Class<?> clz) {
        CriteriaBuilder b = new CriteriaBuilder();
        ResultMappedBuilder builder = b.new ResultMappedBuilder(clz);

        return builder;
    }


    public static DomainObjectBuilder buildDomainObject(Class<?> mainClz, Class<?> withClz) {
        CriteriaBuilder b = new CriteriaBuilder();
        DomainObjectBuilder builder = b.new DomainObjectBuilder(mainClz, withClz);

        return builder;
    }


    public Class<?> getClz() {
        return this.criteria.getClz();
    }


    protected static boolean isNullOrEmpty(Object v) {

        Class<?> vType = v.getClass();

        if (vType == String.class) {
            return StringUtil.isNullOrEmpty(v.toString());
        }

        return false;
    }


    public interface ConditionBuilder {

        CriteriaBuilder eq(String property, Object value);

        CriteriaBuilder lt(String property, Object value);

        CriteriaBuilder lte(String property, Object value);

        CriteriaBuilder gt(String property, Object value);

        CriteriaBuilder gte(String property, Object value);

        CriteriaBuilder ne(String property, Object value);

        CriteriaBuilder like(String property, String value);

        CriteriaBuilder likeRight(String property, String value);

        CriteriaBuilder notLike(String property, String value);

        CriteriaBuilder between(String property, Object min, Object max);

        CriteriaBuilder in(String property, List<? extends Object> list);

        CriteriaBuilder nin(String property, List<Object> list);

        CriteriaBuilder nonNull(String property);

        CriteriaBuilder isNull(String property);

        CriteriaBuilder x(String sql);

        CriteriaBuilder x(String sql, List<Object> valueList);

        void under(X x);

        X getX();

        ConditionBuilder beginSub();

    }

    public Criteria get() {
        DataPermission.Chain.befroeGetCriteria(this, this.criteria);
        Iterator<X> ite = this.criteria.getListX().iterator();
        while (ite.hasNext()) {
            X x = ite.next();
            if (Objects.isNull(x.getConjunction()) && Objects.isNull(x.getPredicate()) && Objects.isNull(x.getKey()))
                ite.remove();
        }
        return this.criteria;
    }


    public class ResultMappedBuilder extends CriteriaBuilder {


        @Override
        public Criteria.ResultMappedCriteria get() {
            return (ResultMappedCriteria) super.get();
        }

        private void init() {
            super.instance = this;
            Criteria.ResultMappedCriteria resultMapped = new Criteria.ResultMappedCriteria();
            super.criteria = resultMapped;
        }

        private void init(Class<?> clz) {
            ResultMappedCriteria f = (Criteria.ResultMappedCriteria) super.criteria;
            f.setClz(clz);
            Parsed parsed = Parser.get(clz);
            f.setParsed(parsed);
        }

        public ResultMappedBuilder(Class<?> clz) {
            init();
            init(clz);
        }

        private void xAddResultKey(List<String> xExpressionList) {
            for (String xExpression : xExpressionList) {
                get().getResultKeyList().add(xExpression);
            }
        }

        private void xAddResultKey(Fetched fetchResult) {
            if (fetchResult == null)
                return;
            Map<String, Object> resultObjMap = fetchResult.getResultKeyMap();
            if (resultObjMap == null || resultObjMap.isEmpty())
                return;
            List<String> xExpressionList = BeanMapUtil.toStringKeyList(resultObjMap);
            xAddResultKey(xExpressionList);
        }

        private void xAddResultKey(MapResult mappedKey) {
            if (mappedKey == null)
                return;
            String[] arr = mappedKey.getResultKeys();
            if (arr == null || arr.length == 0)
                return;
            List<String> list = Arrays.asList(arr);
            xAddResultKey(list);
        }

        public ResultMappedBuilder resultKey(String resultKey) {
            get().getResultKeyList().add(resultKey);
            return this;
        }

        @Override
        public void paged(Paged paged) {
            super.criteria.paged(paged);
            DataPermission.Chain.onBuild(super.criteria, paged);
        }

        public ResultMappedBuilder distinct(Object... objs) {
            if (objs == null)
                throw new RuntimeException("distinct non resultKey");
            ResultMappedCriteria resultMapped = get();
            Distinct distinct = resultMapped.getDistinct();
            if (Objects.isNull(distinct)) {
                distinct = new Distinct();
                resultMapped.setDistinct(distinct);
            }
            for (Object obj : objs) {
                if (obj instanceof String) {
                    distinct.add(obj.toString());
                } else if (obj instanceof Map) {
                    Map map = (Map) obj;
                    Set<Entry> set = map.entrySet();
                    for (Entry entry : set) {
                        Object key = entry.getKey();
                        Object value = entry.getValue();
                        if (value instanceof Map) {
                            Map vMap = (Map) value;
                            for (Object k : vMap.keySet()) {
                                distinct.add(key.toString() + SqlScript.POINT + k.toString());
                            }
                        }
                    }

                } else {
                    throw new RuntimeException("distinct param suggests String, or Map");
                }
            }
            return this;
        }

        public ResultMappedBuilder groupBy(String property) {
            get().setGroupBy(property);
            return this;
        }

        public ResultMappedBuilder reduce(Reduce.ReduceType type, String property) {
            Reduce reduce = new Reduce();
            reduce.setType(type);
            reduce.setProperty(property);
            get().getReduceList().add(reduce);
            return this;
        }

    }


    public class DomainObjectBuilder extends CriteriaBuilder {

        @Override
        public Criteria.DomainObjectCriteria get() {
            return (Criteria.DomainObjectCriteria) super.get();
        }

        private void init() {
            super.instance = this;
//            Criteria c = new Criteria();
            Criteria.DomainObjectCriteria domainObjectCriteria = new Criteria.DomainObjectCriteria();
            super.criteria = domainObjectCriteria;
        }

        private void init(Class<?> mainClz, Class<?> withClz) {
            Criteria.DomainObjectCriteria doc = (Criteria.DomainObjectCriteria) super.criteria;
            doc.setClz(mainClz);
            doc.setWithClz(withClz);
            Parsed parsed = Parser.get(mainClz);
            doc.setParsed(parsed);
        }


        public DomainObjectBuilder(Class mainClz, Class withClz) {
            init();
            init(mainClz, withClz);
        }

        public DomainBuilder domain() {
            return this.domainBuilder;
        }

        private DomainBuilder domainBuilder = new DomainBuilder() {
            @Override
            public DomainBuilder known(List<? extends Object> mainIdList) {
                get().setKnownMainIdList(mainIdList);
                return this;
            }

            @Override
            public DomainBuilder relative(Class relativeClz) {
                get().setRelativeClz(relativeClz);
                return this;
            }

            @Override
            public DomainBuilder on(String mainProperty) {
                get().setMainPropperty(mainProperty);
                return this;
            }

            @Override
            public CriteriaBuilder with(String withProperty) {
                get().setWithProperty(withProperty);
                return instance;
            }
        };

    }

}