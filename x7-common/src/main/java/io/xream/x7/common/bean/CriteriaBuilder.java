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
import io.xream.x7.common.util.BeanMapUtil;
import io.xream.x7.common.util.StringUtil;
import io.xream.x7.common.web.Direction;
import io.xream.x7.common.web.Fetched;
import io.xream.x7.common.web.MapResult;
import io.xream.x7.common.web.Paged;
import org.springframework.util.Assert;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Sim
 */
public class CriteriaBuilder extends ConditionCriteriaBuilder {

    private Criteria criteria;

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


    public CriteriaBuilder forceIndex(String indexName) {
        if (StringUtil.isNullOrEmpty(indexName))
            return this;
        this.criteria.setForceIndex(indexName);
        return this;
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



    private CriteriaBuilder(Criteria criteria) {
        super(criteria.getListX());
        this.criteria = criteria;
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

        Criteria.ResultMappedCriteria resultMappedCriteria = new Criteria.ResultMappedCriteria();

        ResultMappedBuilder builder = new ResultMappedBuilder(resultMappedCriteria);

        if (ro != null) {
            builder.xAddResultKey(ro);
            if (ro instanceof Paged) {
                builder.paged((Paged) ro);
            }
        }

        return builder;
    }

    public static ResultMappedBuilder buildResultMapped(MapResult ro) {

        Criteria.ResultMappedCriteria resultMappedCriteria = new Criteria.ResultMappedCriteria();

        ResultMappedBuilder builder = new ResultMappedBuilder(resultMappedCriteria);

        if (ro != null) {

            builder.xAddResultKey(ro);

            if (ro instanceof Paged) {
                builder.paged((Paged) ro);
            }
        }

        return builder;
    }


    public static ResultMappedBuilder buildResultMapped() {
        Criteria.ResultMappedCriteria resultMappedCriteria = new Criteria.ResultMappedCriteria();

        return new ResultMappedBuilder(resultMappedCriteria);

    }

    public Class<?> getClz() {
        return this.criteria.getClz();
    }

    public Criteria get() {
        return this.criteria;
    }

    protected SourceScript sourceScriptTemp;

    public static class ResultMappedBuilder extends CriteriaBuilder {

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
            public SourceScriptBuilder on(String key, JoinFrom joinFrom) {
                if (key.contains("."))
                    throw new IllegalArgumentException("On key can not contains '.'");
                On on = new On();
                on.setKey(key);
                on.setOp(Op.EQ.sql());
                on.setJoinFrom(joinFrom);
                sourceScriptTemp.setOn(on);
                return this;
            }

            @Override
            public SourceScriptBuilder on(String key, Op op, JoinFrom joinFrom) {
                if (key.contains("."))
                    throw new IllegalArgumentException("On key can not contains '.'");
                On on = new On();
                on.setKey(key);
                on.setOp(op.sql());
                on.setJoinFrom(joinFrom);
                sourceScriptTemp.setOn(on);
                return this;
            }

            @Override
            public ConditionCriteriaBuilder more() {
                return ConditionCriteriaBuilder.build(sourceScriptTemp.getListX());
            }

        };

        public SourceScriptBuilder sourceScript() {
            sourceScriptTemp = new SourceScript();
            get().getSourceScripts().add(sourceScriptTemp);
            return this.sourceScriptBuilder;
        }

        public ResultMappedBuilder withoutOptimization() {
            get().setWithoutOptimization(true);
            return this;
        }

        @Override
        public Criteria.ResultMappedCriteria get() {
            return (ResultMappedCriteria) super.get();
        }

        private void init() {

            Criteria.ResultMappedCriteria resultMapped = new Criteria.ResultMappedCriteria();
            super.criteria = resultMapped;

        }

        private void init(Class<?> clz) {
            ResultMappedCriteria f = (Criteria.ResultMappedCriteria) super.criteria;
            f.setClz(clz);
            Parsed parsed = Parser.get(clz);
            f.setParsed(parsed);
        }

        public ResultMappedBuilder(Criteria criteria) {
            super(criteria);
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

        public ResultMappedBuilder resultWithDottedKey() {
            get().setResultWithDottedKey(true);
            return this;
        }

        /**
         * @param functionScript FUNCTION(?,?)
         * @param keys           test.createAt, test.endAt
         */
        public ResultMappedBuilder resultKeyFunction(ResultKeyAlia functionAlia_wrap, String functionScript, String... keys) {
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
                                distinct.add(key.toString() + SqlScript.DOT + k.toString());
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
         * @param type
         * @param property
         * @param having   paged().totalRowsIgnored(true), if isTotalRowsIgnored == false，will throw Exception
         */
        public ResultMappedBuilder reduce(ReduceType type, String property, Having having) {
            Reduce reduce = new Reduce();
            reduce.setType(type);
            reduce.setProperty(property);
            reduce.setHaving(having);
            get().getReduceList().add(reduce);
            return this;
        }

    }

}