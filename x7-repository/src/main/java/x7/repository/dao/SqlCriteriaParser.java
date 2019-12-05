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
package x7.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import x7.core.bean.*;
import x7.core.util.BeanUtil;
import x7.core.util.BeanUtilX;
import x7.core.util.StringUtil;
import x7.core.web.Direction;
import x7.repository.CriteriaParser;
import x7.repository.SqlParsed;
import x7.repository.mapper.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SqlCriteriaParser implements CriteriaParser {

    @Autowired
    private Mapper.Dialect dialect;

    public void setDialect(Mapper.Dialect dialect) {
        this.dialect = dialect;
    }


    private void mapping(String script, Criteria criteria, StringBuilder sb) {
        String[] keyArr = script.split(SqlScript.SPACE);//
        int length = keyArr.length;
        for (int i = 0; i < length; i++) {
            String origin = keyArr[i].trim();

            String target = mapping(origin, criteria);
            sb.append(target).append(SqlScript.SPACE);

        }
    }

    private String mapping(String key, Criteria criteria) {

        if (key.contains(SqlScript.POINT)) {

            String[] arr = key.split("\\.");
            String alia = arr[0];
            String property = arr[1];

            String clzName = BeanUtilX.getClzName(alia, criteria);

            Parsed parsed = Parser.get(clzName);
            if (parsed == null)
                throw new RuntimeException("Entity Bean Not Exist: " + BeanUtil.getByFirstUpper(key));

            String value = parsed.getTableName(alia) + SqlScript.POINT + parsed.getMapper(property);

            return value;
        }

        if (criteria instanceof Criteria.ResultMappedCriteria) {

            Parsed parsed = Parser.get(key);
            if (parsed != null) {
                return parsed.getTableName();
            }

        }

        Class clz = criteria.getClz();
        Parsed parsed = Parser.get(clz);
        if (key.equals(BeanUtilX.getByFirstLower(parsed.getClz().getSimpleName())))
            return parsed.getTableName();
        String value = parsed.getMapper(key);
        if (value == null)
            return key;
        return value;

    }

    @Override
    public String parseCondition(CriteriaCondition criteriaCondition) {
        if (Objects.isNull(criteriaCondition))
            return "";
        StringBuilder sb = new StringBuilder();
        List<Criteria.X> xList = criteriaCondition.getListX();
        x(sb, xList, criteriaCondition, true);
        return sb.toString();
    }

    @Override
    public SqlParsed parse(Criteria criteria) {

        parseAlia(criteria);

        StringBuilder sb = new StringBuilder();

        env(criteria);

        resultKey(criteria);
        /*
         * select column
         */
        select(sb, criteria);
        /*
         * from table
         */
        sourceScript(sb, criteria);

        /*
         * StringList
         */
        x(sb, criteria.getListX(), criteria);
        /*
         * group by
         */
        groupBy(sb, criteria);

        StringBuilder countSql = count(sb,criteria);
        /*
         * sort
         */
        sort(sb, criteria);

        SqlParsed sqlParsed = sqlArr(sb, criteria,countSql);

        return sqlParsed;
    }

    private SqlParsed sqlArr(StringBuilder sb, Criteria criteria, StringBuilder countSql){

        SqlParsed sqlParsed = new SqlParsed();

        sqlParsed.setCountSql(countSql == null ? null : countSql.toString());

        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append(SqlScript.SELECT).append(SqlScript.SPACE).append(criteria.resultAllScript()).append(SqlScript.SPACE).append(sb);

        sqlParsed.setSql(sqlSb);

        return sqlParsed;
    }


    private void env(Criteria criteria) {
        if (criteria instanceof Criteria.ResultMappedCriteria) {
            Criteria.ResultMappedCriteria resultMapped = (Criteria.ResultMappedCriteria) criteria;
            MapMapper mapMapper = resultMapped.getMapMapper();//
            if (Objects.isNull(mapMapper)) {
                mapMapper = new MapMapper();
                resultMapped.setMapMapper(mapMapper);
            }
        }
    }

    private void resultKey(Criteria criteria) {
        if (!(criteria instanceof Criteria.ResultMappedCriteria))
            return;

        boolean flag = false;

        Criteria.ResultMappedCriteria resultMapped = (Criteria.ResultMappedCriteria) criteria;
        StringBuilder column = new StringBuilder();

        MapMapper mapMapper = resultMapped.getMapMapper();

        if (Objects.nonNull(resultMapped.getDistinct())) {

            if (!flag) resultMapped.getResultKeyList().clear();//去掉构造方法里设置的返回key

            column.append(SqlScript.DISTINCT);
            List<String> list = resultMapped.getDistinct().getList();
            int size = list.size();
            int i = 0;
            for (String resultKey : list) {

                String value = mapping(resultKey, criteria);
                column.append(SqlScript.SPACE).append(value);
                resultMapped.getResultKeyList().add(resultKey);//返回值
                mapMapper.put(resultKey, value);//REDUCE ALIAN NAME
                i++;
                if (i < size) {
                    column.append(SqlScript.COMMA);
                }
            }
            criteria.setCountDistinct("COUNT(" + column.toString() + ") count");
            flag = true;
        }

        List<Reduce> reduceList = resultMapped.getReduceList();

        if (!reduceList.isEmpty()) {

            if (!flag) resultMapped.getResultKeyList().clear();//去掉构造方法里设置的返回key

            for (Reduce reduce : reduceList) {
                if (flag) {
                    column.append(SqlScript.COMMA);
                }
                String alianProperty = reduce.getProperty() + SqlScript.UNDER_LINE + reduce.getType().toString().toLowerCase();//property_count
                String alianName = alianProperty.replace(SqlScript.POINT, SqlScript.UNDER_LINE);
                String value = mapping(reduce.getProperty(), criteria);
                column.append(SqlScript.SPACE)
                        .append(reduce.getType())
                        .append(SqlScript.LEFT_PARENTTHESIS)//" ( "
                        .append(value)
                        .append(SqlScript.RIGHT_PARENTTHESIS).append(SqlScript.SPACE)//" ) "
                        .append(alianName);

//                String alianProperty = reduce.getProperty() + BeanUtil.getByFirstUpper(reduce.getType().toString().toLowerCase());
                mapMapper.put(alianProperty, alianName);//REDUCE ALIAN NAME
                resultMapped.getResultKeyList().add(alianProperty);
                flag = true;
            }
        }


        String cs = column.toString();
        if (StringUtil.isNullOrEmpty(cs)) {
            List<String> resultList = resultMapped.getResultKeyList();

            StringBuilder sb = new StringBuilder();
            if (resultList.isEmpty()) {
                throw new RuntimeException("Suggest API: find(Criteria criteria)");
            } else {
                int size = resultList.size();
                for (int i = 0; i < size; i++) {
                    String key = resultList.get(i);
                    String mapper = mapping(key,criteria);
                    mapMapper.put(key, mapper);
                    mapper = this.dialect.filterResultKey(mapper,resultMapped);
                    sb.append(SqlScript.SPACE ).append(mapper);
                    if (i < size - 1) {
                        sb.append(SqlScript.COMMA );
                    }
                }
            }

            criteria.setCustomedResultKey(sb.toString());
        } else {
            criteria.setCustomedResultKey(column.toString());
        }
    }

    private void select(StringBuilder sb, Criteria criteria) {

    }

    private void groupBy(StringBuilder sb, Criteria criteria) {
        if (criteria instanceof Criteria.ResultMappedCriteria) {
            Criteria.ResultMappedCriteria rm = (Criteria.ResultMappedCriteria) criteria;

            String groupByS = rm.getGroupBy();
            if (StringUtil.isNullOrEmpty(groupByS))
                return;

            sb.append(Conjunction.GROUP_BY.sql());

            String[] arr = groupByS.split(SqlScript.COMMA);

            int i = 0, l = arr.length;
            for (String groupBy : arr) {
                groupBy = groupBy.trim();
                if (StringUtil.isNotNull(groupBy)) {
                    String mapper = mapping(groupBy, criteria);
                    sb.append(mapper);
                    i++;
                    if (i < l) {
                        sb.append(SqlScript.COMMA);
                    }
                }
            }
        }
    }


    private void parseAlia(Criteria criteria) {
        String script = criteria.sourceScript().trim();

        if (criteria instanceof Criteria.ResultMappedCriteria) {
            Criteria.ResultMappedCriteria resultMappedCriteria = (Criteria.ResultMappedCriteria) criteria;
            Map<String, String> aliaMap = BeanUtilX.parseAliaBySourceScriptSql(script);
            resultMappedCriteria.setAliaMap(aliaMap);
        }
    }

    private void sourceScript(StringBuilder sb, Criteria criteria) {
        String script = criteria.sourceScript().trim();

        if (script.startsWith(SqlScript.FROM) || script.startsWith(SqlScript.FROM.toLowerCase())) {
            script = script.replaceFirst(SqlScript.FROM, SqlScript.NONE);
        }

        sb.append(SqlScript.SPACE).append(SqlScript.FROM).append(SqlScript.SPACE);

        mapping(script, criteria, sb);

    }

    private StringBuilder count(StringBuilder sb, Criteria criteria) {
        if (!criteria.isScroll()) {
            StringBuilder countSb = new StringBuilder();
            countSb.append(SqlScript.SELECT).append(SqlScript.SPACE).append(criteria.getCountDistinct()).append(SqlScript.SPACE).append(sb);
            return countSb;
        }
        return null;
    }

    private void sort(StringBuilder sb, Criteria criteria) {

        if (criteria.isFixedSort())
            return;

        List<Sort> sortList = criteria.getSortList();
        if (sortList !=null && !sortList.isEmpty()){

            sb.append(Conjunction.ORDER_BY.sql());
            int size = sortList.size();
            int i = 0;
            for (Sort sort : sortList) {
                String orderBy = sort.getOrderBy();
                String mapper = mapping(orderBy, criteria);
                sb.append(mapper).append(SqlScript.SPACE);
                Direction direction = sort.getDirection();
                if (direction == null) {
                    sb.append(Direction.DESC);
                }else{
                    sb.append(direction);
                }
                i++;
                if (i < size) {
                    sb.append(SqlScript.COMMA).append(SqlScript.SPACE);
                }
            }
        }

    }

    private StringBuilder sortScript(Criteria criteria) {

        StringBuilder sb = new StringBuilder();

        if (criteria.isFixedSort())
            return null;

        List<Sort> sortList = criteria.getSortList();
        if (sortList !=null && !sortList.isEmpty()){

            sb.append(Conjunction.ORDER_BY.sql());
            int size = sortList.size();
            int i = 0;
            for (Sort sort : sortList) {
                String orderBy = sort.getOrderBy();
                String mapper = mapping(orderBy, criteria);
                sb.append(mapper).append(SqlScript.SPACE);
                Direction direction = sort.getDirection();
                if (direction == null) {
                    sb.append(Direction.DESC);
                }else{
                    sb.append(direction);
                }
                i++;
                if (i < size) {
                    sb.append(SqlScript.COMMA).append(SqlScript.SPACE);
                }
            }
        }
        return sb;
    }

    private void x(StringBuilder sb, List<Criteria.X> xList, CriteriaCondition criteria, boolean isWhere) {
        for (Criteria.X x : xList) {

            if (x.getPredicate() == Predicate.X) {
                appendConjunction(sb, x, criteria, isWhere);
                sb.append(x.getKey());
                Object valueObject = x.getValue();
                if (valueObject != null) {
                    if (valueObject instanceof List){
                        criteria.getValueList().addAll((List<Object>)valueObject);
                    }else{
                        criteria.getValueList().add(valueObject);
                    }
                }
                continue;
            }


            Object v = x.getValue();
            if (Objects.isNull(v))
                continue;


            if (Objects.nonNull(x.getConjunction())) {

                List<Criteria.X> subList = x.getSubList();
                if (x.getSubList() != null) {
                    StringBuilder xSb = new StringBuilder();

                    x(xSb, subList, criteria, false);//sub concat

                    String script = xSb.toString();
                    if (StringUtil.isNotNull(script)) {
                        final String and = Conjunction.AND.sql();
                        final String or = Conjunction.OR.sql();
                        if (script.startsWith(and)) {
                            script = script.replaceFirst(and, SqlScript.NONE);
                        } else if (script.startsWith(or)) {
                            script = script.replaceFirst(or, SqlScript.NONE);
                        }
                        x.setScript(Predicate.SUB_BEGIN.sql() + script + Predicate.SUB_END.sql());
                    }
                }

            }

            if (Predicate.SUB_BEGIN == x.getPredicate()) {
                continue;
            } else if (Predicate.SUB_END == x.getPredicate()) {
                continue;
            }

            if (StringUtil.isNotNull(x.getKey())) {
                if (x.getKey().equals(Predicate.SUB.sql())) {
                    if (Objects.nonNull(x.getScript())) {

                        appendConjunction(sb, x, criteria, isWhere);
                        sb.append(x.getScript());
                    }
                    continue;
                }
            }
            x(x, criteria, isWhere);

            if (Objects.nonNull(x.getScript())) {
                sb.append(x.getScript());
            }
        }

    }

    private void x(StringBuilder sb, List<Criteria.X> xList, Criteria criteria) {

        StringBuilder xsb = new StringBuilder();
        x(xsb, xList, criteria, true);

        String script = xsb.toString();

        mapping(script, criteria, sb);

    }


    private void appendConjunction(StringBuilder sb, Criteria.X x, CriteriaCondition criteriaBuilder, boolean isWhere) {
        if (Objects.isNull(x.getConjunction()))
            return;
        if (criteriaBuilder instanceof Criteria) {
            Criteria criteria = (Criteria) criteriaBuilder;
            if (isWhere && criteria.isWhere()) {
                criteria.setWhere(false);
                sb.append(Conjunction.WHERE.sql());
            } else {
                sb.append(x.getConjunction().sql());
            }
        } else {
            sb.append(x.getConjunction().sql());
        }
    }

    private void x(Criteria.X x, CriteriaCondition criteria, boolean isWhere) {

        StringBuilder sb = new StringBuilder();
        Predicate p = x.getPredicate();
        Object v = x.getValue();

        if (p == Predicate.IN || p == Predicate.NOT_IN) {

            appendConjunction(sb, x, criteria, isWhere);

            sb.append(x.getKey()).append(p.sql());
            List<Object> inList = (List<Object>) v;
            in(sb, inList);
        } else if (p == Predicate.BETWEEN) {

            appendConjunction(sb, x, criteria, isWhere);

            sb.append(x.getKey()).append(p.sql());
            between(sb);

            MinMax minMax = (MinMax) v;
            List<Object> valueList = criteria.getValueList();
            valueList.add(minMax.getMin());
            valueList.add(minMax.getMax());

        } else if (p == Predicate.IS_NOT_NULL || p == Predicate.IS_NULL) {

            appendConjunction(sb, x, criteria, isWhere);

            sb.append(v).append(p.sql());

        } else {
            if (StringUtil.isNullOrEmpty(x.getKey()))
                return;

            appendConjunction(sb, x, criteria, isWhere);

            Class clz = v.getClass();
            sb.append(x.getKey()).append(x.getPredicate().sql());
            if (clz == String.class) {
                String str = v.toString();
                if (str.startsWith(SqlScript.WELL_NO) && str.endsWith(SqlScript.WELL_NO)) {
                    str = str.replace(SqlScript.WELL_NO, SqlScript.NONE);
                    sb.append(str);
                    return;
                } else {
                    sb.append(SqlScript.PLACE_HOLDER);
                }
            } else {
                sb.append(SqlScript.PLACE_HOLDER);
            }

            if (clz.getSuperclass().isEnum() || clz.isEnum()) {
                try {
                    criteria.getValueList().add(((Enum)v).name());
                }catch (Exception e){

                }
            } else {
                criteria.getValueList().add(v);
            }
        }
        x.setScript(sb.toString());
    }

    private void between(StringBuilder sb) {

        sb.append(SqlScript.PLACE_HOLDER).append(Conjunction.AND.sql()).append(SqlScript.PLACE_HOLDER);

    }

    private void in(StringBuilder sb, List<Object> inList) {

        if (inList == null || inList.isEmpty())
            return;

        Object v = inList.get(0);

        Class<?> vType = v.getClass();

        boolean isNumber = (vType == long.class || vType == int.class || vType == Long.class || vType == Integer.class);

        sb.append(SqlScript.LEFT_PARENTTHESIS).append(SqlScript.SPACE);//"( "

        int length = inList.size();
        if (isNumber) {
            for (int j = 0; j < length; j++) {
                Object value = inList.get(j);
                if (value == null)
                    continue;
                sb.append(value);
                if (j < length - 1) {
                    sb.append(SqlScript.COMMA);
                }
            }
        } else {
            for (int j = 0; j < length; j++) {
                Object value = inList.get(j);
                if (value == null || StringUtil.isNullOrEmpty(value.toString()))
                    continue;
                sb.append(SqlScript.SINGLE_QUOTES).append(value).append(SqlScript.SINGLE_QUOTES);//'string'
                if (j < length - 1) {
                    sb.append(SqlScript.COMMA);
                }
            }
        }

        sb.append(SqlScript.SPACE).append(SqlScript.RIGHT_PARENTTHESIS);//"  )"

    }


}
