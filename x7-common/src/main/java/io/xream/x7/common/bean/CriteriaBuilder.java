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

import io.xream.x7.common.bean.Criteria.ResultMappedCriteria;
import io.xream.x7.common.bean.Criteria.X;
import io.xream.x7.common.util.BeanMapUtil;
import io.xream.x7.common.util.BeanUtilX;
import io.xream.x7.common.util.StringUtil;
import io.xream.x7.common.web.Direction;
import io.xream.x7.common.web.Fetched;
import io.xream.x7.common.web.MapResult;
import io.xream.x7.common.web.Paged;
import org.springframework.util.Assert;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Sim
 */
public class CriteriaBuilder {

    private Criteria criteria;
    private CriteriaBuilder instance;

    public CriteriaBuilder routeKey(Object routeKey) {
        this.criteria.setRouteKey(routeKey);
        return this;
    }

    public PageBuilder paged() {
        return this.pageBuilder;
    }

    public void paged(Paged paged) {
        criteria.paged(paged);
    }


    private ConditionBuilder andOr(ConjunctionAndOtherScript c) {

        X x = new X();
        x.setConjunction(c);
        x.setValue(c);

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

    public ConditionBuilder and() {
        return andOr(ConjunctionAndOtherScript.AND);
    }

    public ConditionBuilder or() {
        return andOr(ConjunctionAndOtherScript.OR);
    }

    public CriteriaBuilder forceIndex(String indexName) {
        if (StringUtil.isNullOrEmpty(indexName))
            return instance;
        this.criteria.setForceIndex(indexName);
        return instance;
    }

    public CriteriaBuilder endSub() {

        X x = new X();
        x.setPredicate(PredicateAndOtherScript.SUB_END);
        x.setValue(PredicateAndOtherScript.SUB_END);

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
        public PageBuilder ignoreTotalRows() {
            criteria.setTotalRowsIgnored(true);
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
            if (sortList == null) {
                sortList = new ArrayList<>();
                criteria.setSortList(sortList);
            }
            Sort sort = new Sort(orderBy, direction);
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


        private CriteriaBuilder doGle(PredicateAndOtherScript p, String property, Object value, Parsed parsed) {
            if (value == null)
                return instance;
            if (Objects.nonNull(parsed)) {
                if (BeanUtilX.isBaseType_0(property, value,parsed))
                    return instance;
            }
            if (StringUtil.isNullOrEmpty(value))
                return instance;

            x.setPredicate(p);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder eq(String property, Object value) {
            return doGle(PredicateAndOtherScript.EQ,property,value,criteria.getParsed());
        }

        @Override
        public CriteriaBuilder lt(String property, Object value) {
            return doGle(PredicateAndOtherScript.LT,property,value,criteria.getParsed());
        }

        @Override
        public CriteriaBuilder lte(String property, Object value) {
            return doGle(PredicateAndOtherScript.LTE,property,value,criteria.getParsed());
        }

        @Override
        public CriteriaBuilder gt(String property, Object value) {
            return doGle(PredicateAndOtherScript.GT,property,value,criteria.getParsed());
        }

        @Override
        public CriteriaBuilder gte(String property, Object value) {
            return doGle(PredicateAndOtherScript.GTE,property,value,criteria.getParsed());
        }

        @Override
        public CriteriaBuilder ne(String property, Object value) {
            return doGle(PredicateAndOtherScript.NE,property,value,criteria.getParsed());
        }

        private CriteriaBuilder doLike(PredicateAndOtherScript p,String property, String likeWalue){

            x.setPredicate(p);
            x.setKey(property);
            x.setValue(likeWalue);

            return instance;
        }

        @Override
        public CriteriaBuilder like(String property, String value) {

            if (StringUtil.isNullOrEmpty(value))
                return instance;

            String likeValue = SqlScript.LIKE_HOLDER + value + SqlScript.LIKE_HOLDER;
            return doLike(PredicateAndOtherScript.LIKE,property,likeValue);
        }

        @Override
        public CriteriaBuilder likeRight(String property, String value) {

            if (StringUtil.isNullOrEmpty(value))
                return instance;

            String likeValue = value + SqlScript.LIKE_HOLDER;
            return doLike(PredicateAndOtherScript.LIKE,property,likeValue);
        }

        @Override
        public CriteriaBuilder notLike(String property, String value) {

            if (StringUtil.isNullOrEmpty(value))
                return instance;

            String likeValue = SqlScript.LIKE_HOLDER + value + SqlScript.LIKE_HOLDER;
            return doLike(PredicateAndOtherScript.NOT_LIKE,property,likeValue);
        }

        @Override
        public CriteriaBuilder between(String property, Object min, Object max) {

            if (min == null || max == null)
                return instance;

            if (StringUtil.isNullOrEmpty(max))
                return instance;
            if (StringUtil.isNullOrEmpty(min))
                return instance;

            MinMax minMax = new MinMax();
            minMax.setMin(min);
            minMax.setMax(max);

            x.setPredicate(PredicateAndOtherScript.BETWEEN);
            x.setKey(property);
            x.setValue(minMax);

            return instance;
        }

        private CriteriaBuilder doIn(PredicateAndOtherScript p, String property,List<? extends Object> list, Parsed parsed ){

            if (list == null || list.isEmpty())
                return instance;

            List<Object> tempList = new ArrayList<>();
            for (Object obj : list) {
                if (Objects.isNull(obj))
                    continue;
                if (Objects.nonNull(parsed)) {
                    if (BeanUtilX.isBaseType_0(property, obj,parsed))
                        continue;
                }
                if (!tempList.contains(obj)) {
                    tempList.add(obj);
                }
            }

            if (tempList.isEmpty())
                return instance;

            x.setPredicate(p);
            x.setKey(property);
            x.setValue(tempList);

            return instance;
        }

        @Override
        public CriteriaBuilder in(String property, List<? extends Object> list) {
            return doIn(PredicateAndOtherScript.IN, property, list,criteria.getParsed());
        }

        @Override
        public CriteriaBuilder nin(String property, List<? extends Object> list) {
            return doIn(PredicateAndOtherScript.NOT_IN, property, list,criteria.getParsed());
        }

        private CriteriaBuilder doNull(PredicateAndOtherScript p, String property){
            if (StringUtil.isNullOrEmpty(property))
                return instance;

            x.setPredicate(p);
            x.setValue(property);

            return instance;
        }

        @Override
        public CriteriaBuilder nonNull(String property) {
            return doNull(PredicateAndOtherScript.IS_NOT_NULL, property);
        }

        @Override
        public CriteriaBuilder isNull(String property) {
            return doNull(PredicateAndOtherScript.IS_NULL, property);
        }

        @Override
        public CriteriaBuilder x(String sql) {
            return x(sql,null);
        }

        @Override
        public CriteriaBuilder x(String sql, List<? extends Object> valueList) {

            if (StringUtil.isNullOrEmpty(sql))
                return instance;

            sql = BeanUtilX.normalizeSql(sql);

            x.setPredicate(PredicateAndOtherScript.X);
            x.setKey(sql);
            x.setValue(valueList);

            return instance;
        }

        @Override
        public ConditionBuilder beginSub() {

            x.setKey(PredicateAndOtherScript.SUB.sql());// special treat FIXME
            x.setValue(PredicateAndOtherScript.SUB);

            List<X> subList = new ArrayList<>();
            x.setSubList(subList);

            X from = new X();
            from.setPredicate(PredicateAndOtherScript.SUB_BEGIN);
            from.setValue(PredicateAndOtherScript.SUB_BEGIN);

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

    @Deprecated
    public static ResultMappedBuilder buildResultMapped(Fetched ro) {
        CriteriaBuilder b = new CriteriaBuilder();
        ResultMappedBuilder builder = b.new ResultMappedBuilder();

        if (ro != null) {
            builder.xAddResultKey(ro);
            if (ro instanceof Paged) {
                builder.paged((Paged) ro);
            }
        }

        return builder;
    }

    public static ResultMappedBuilder buildResultMapped(MapResult ro) {
        CriteriaBuilder b = new CriteriaBuilder();
        ResultMappedBuilder builder = b.new ResultMappedBuilder();

        if (ro != null) {

            builder.xAddResultKey(ro);

            if (ro instanceof Paged) {
                builder.paged((Paged) ro);
            }
        }

        return builder;
    }


    public static ResultMappedBuilder buildResultMapped() {
        CriteriaBuilder b = new CriteriaBuilder();
        ResultMappedBuilder builder = b.new ResultMappedBuilder();

        return builder;
    }

    public Class<?> getClz() {
        return this.criteria.getClz();
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

        CriteriaBuilder nin(String property, List<? extends Object> list);

        CriteriaBuilder nonNull(String property);

        CriteriaBuilder isNull(String property);

        CriteriaBuilder x(String sql);

        CriteriaBuilder x(String sql, List<? extends Object> valueList);

        void under(X x);

        X getX();

        ConditionBuilder beginSub();

    }

    public Criteria get() {
        Iterator<X> ite = this.criteria.getListX().iterator();
        while (ite.hasNext()) {
            X x = ite.next();
            if (Objects.isNull(x.getConjunction()) && Objects.isNull(x.getPredicate()) && Objects.isNull(x.getKey()))
                ite.remove();
        }
        return this.criteria;
    }

    protected SourceScript sourceScriptTemp;
    public class ResultMappedBuilder extends CriteriaBuilder {

        private SourceScriptBuilder sourceScriptBuilder = new SourceScriptBuilder() {

            @Override
            public SourceScriptBuilder source(String source) {
                sourceScriptTemp.setSource(source);
                return this;
            }

            @Override
            public SourceScriptBuilder alia(String alia) {
                sourceScriptTemp.setAlia(alia);
                return this;
            }

            @Override
            public SourceScriptBuilder joinType(JoinType joinType) {
                sourceScriptTemp.setJoinType(joinType);
                return this;
            }

            @Override
            public SourceScriptBuilder on(String key, On.Op op,JoinFrom joinTarget) {
                On on = new On();
                on.setKey(key);
                on.setOp(op.sql());
                on.setJoinTarget(joinTarget);
                if (sourceScriptTemp.getOnList().isEmpty()){
                    on.setAndOr("ON");
                }else{
                    on.setAndOr(AndOr.AND.name());
                }
                sourceScriptTemp.getOnList().add(on);
                return this;
            }

            @Override
            public SourceScriptBuilder onOr(String key, On.Op op,JoinFrom joinTarget) {
                On on = new On();
                on.setKey(key);
                on.setOp(op.sql());
                on.setJoinTarget(joinTarget);
                if (sourceScriptTemp.getOnList().isEmpty()){
                    on.setAndOr("ON");
                }else{
                    on.setAndOr(AndOr.OR.name());
                }
                sourceScriptTemp.getOnList().add(on);
                return this;
            }
        };

        public SourceScriptBuilder sourceScript(){
            sourceScriptTemp = new SourceScript();
            get().getSourceScripts().add(sourceScriptTemp);
            return this.sourceScriptBuilder;
        }

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

        public ResultMappedBuilder() {
            init();
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
            if (StringUtil.isNullOrEmpty(resultKey))
                return this;
            get().getResultKeyList().add(resultKey);
            return this;
        }

        /**
         *
         * @param functionScript FUNCTION(?,?)
         *
         * @param keys  test.createAt, test.endAt
         *
         */
        public ResultMappedBuilder resultKeyFunction( FunctionAlia functionAlia_wrap, String functionScript, String...keys) {
            if (StringUtil.isNullOrEmpty(functionScript) || keys == null)
                return this;
            Assert.notNull(functionAlia_wrap, "function no alia");
            Assert.notNull(functionAlia_wrap.getPropertyOrAlia());
            FunctionResultKey functionResultKey = new FunctionResultKey();
            functionResultKey.setScript(functionScript);
            functionResultKey.setAlia(functionAlia_wrap.getAlia());
            functionResultKey.setKeys(keys);
            get().getResultFuntionList().add(functionResultKey);
            return this;
        }

        @Override
        public void paged(Paged paged) {
            super.criteria.paged(paged);
        }

        public ResultMappedBuilder sourceScript(String sourceScript) {
            if (StringUtil.isNullOrEmpty(sourceScript))
                return this;
            get().setSourceScript(sourceScript);
            return this;
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

        public ResultMappedBuilder reduce(ReduceType type, String property) {
            Reduce reduce = new Reduce();
            reduce.setType(type);
            reduce.setProperty(property);
            get().getReduceList().add(reduce);
            return this;
        }

        /**
         *
         * @param type
         * @param property
         * @param having   paged().totalRowsIgnored(true), if isTotalRowsIgnored == false，will throw Exception
         */
        public ResultMappedBuilder reduce(ReduceType type, String property, Having having) {
            Reduce reduce = new Reduce();
            reduce.setType(type);
            reduce.setProperty(property);
            reduce.setHaving(having.x());
            get().getReduceList().add(reduce);
            return this;
        }


    }


}