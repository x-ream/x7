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
package io.xream.x7.repository.dao;

import io.xream.x7.common.bean.*;
import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.common.util.*;
import io.xream.x7.repository.CriteriaParser;
import io.xream.x7.repository.SqlParsed;
import io.xream.x7.repository.exception.PersistenceException;
import io.xream.x7.repository.exception.SqlBuildException;
import io.xream.x7.repository.mapper.DataObjectConverter;
import io.xream.x7.repository.mapper.Dialect;
import io.xream.x7.repository.util.SqlParserUtil;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class SqlUtil {

    protected static void adpterSqlKey(PreparedStatement pstmt, String keyOne, Object obj, int i) {
        /*
         * 处理KEY
         */
        Method method = null;
        try {
            method = obj.getClass().getDeclaredMethod(BeanUtil.getGetter(keyOne));
        } catch (NoSuchMethodException e) {
            try {
                method = obj.getClass().getSuperclass().getDeclaredMethod(BeanUtil.getGetter(keyOne));
            } catch (Exception ee) {
                throw new RuntimeException(ExceptionUtil.getMessage(ee));
            }
        }
        try {
            Object value = method.invoke(obj);
            pstmt.setObject(i++, value);
        } catch (Exception e) {
            throw new PersistenceException(ExceptionUtil.getMessage(e));
        }
    }

    protected static String paged(String sql, int page, int rows, Dialect dialect) {
        int start = (page - 1) * rows;
        return dialect.match(sql, start, rows);
    }

    /**
     * 拼接SQL
     */
    protected static String concat(Parsed parsed, String sql, Map<String, Object> queryMap) {

        StringBuilder sb = new StringBuilder();

        boolean flag = (sql.contains(SqlScript.WHERE) || sql.contains(SqlScript.WHERE.toLowerCase()));

        for (String key : queryMap.keySet()) {

            String mapper = parsed.getMapper(key);
            if (flag) {
                sb.append(Conjunction.AND.sql()).append(mapper).append(SqlScript.EQ_PLACE_HOLDER);
            } else {
                sb.append(SqlScript.WHERE).append(mapper).append(SqlScript.EQ_PLACE_HOLDER);
                flag = true;
            }

        }

        sql += sb.toString();

        return sql;
    }


    protected static String buildRefresh(Parsed parsed, RefreshCondition refreshCondition, CriteriaParser criteriaParser) {
        StringBuilder sb = new StringBuilder();
        sb.append(SqlScript.UPDATE).append(SqlScript.SPACE).append(parsed.getTableName()).append(SqlScript.SPACE);
        return concatRefresh(sb, parsed, refreshCondition, criteriaParser);
    }


    private static String concatRefresh(StringBuilder sb, Parsed parsed,
                                        RefreshCondition refreshCondition, CriteriaParser criteriaParser) {

        sb.append(SqlScript.SET);

        List<Criteria.X> refreshList = refreshCondition.getRefreshList();

        List<Object> refreshValueList = new ArrayList<>();

        boolean isNotFirst = false;
        for (Criteria.X x : refreshList) {


            if (x.getPredicate() == Predicate.X) {

                if (isNotFirst) {
                    sb.append(SqlScript.COMMA).append(SqlScript.SPACE);
                }

                isNotFirst = true;

                Object key = x.getKey();

                String str = key.toString();

//                if (str.contains(","))
//                    throw new RuntimeException("RefreshCondition.refresh(), para can not contains(,)");

                String sql = BeanUtilX.normalizeSql(str);

                sql = SqlParserUtil.mapper(sql, parsed);

                sb.append(sql);

            } else {
                String key = x.getKey();
                if (key.contains("?")) {

                    if (isNotFirst) {
                        sb.append(SqlScript.COMMA).append(SqlScript.SPACE);
                    }

                    isNotFirst = true;

                    String sql = BeanUtilX.normalizeSql(key);
                    sql = SqlParserUtil.mapper(sql, parsed);
                    sb.append(sql);
                } else {

                    if (StringUtil.isNullOrEmpty(x.getValue().toString()) || BeanUtilX.isBaseType_0(key, x.getValue(), parsed)) {
                        continue;
                    }

                    if (isNotFirst) {
                        sb.append(SqlScript.COMMA).append(SqlScript.SPACE);
                    }

                    isNotFirst = true;

                    String mapper = parsed.getMapper(key);
                    sb.append(mapper);
                    sb.append(SqlScript.EQ_PLACE_HOLDER);

                    BeanElement be = parsed.getElementMap().get(key);
                    if (be == null) {
                        throw new RuntimeException("can not find the property " + key + " of " + parsed.getClzName());
                    }
                    if (be.clz == Date.class) {
                        if (x.getValue() instanceof Long) {
                            x.setValue(new Date(((Long) x.getValue()).longValue()));
                        }
                    } else if (be.clz == Timestamp.class) {
                        if (x.getValue() instanceof Long) {
                            x.setValue(new Timestamp(((Long) x.getValue()).longValue()));
                        }
                    } else if (be.isJson) {
                        Object v = x.getValue();
                        if (v != null) {
                            String str = JsonX.toJson(v);
                            x.setValue(str);
                        }
                    }

                }

                refreshValueList.add(x.getValue());
            }

        }


        CriteriaCondition condition = refreshCondition.getCondition();
        if (!refreshValueList.isEmpty()) {
            condition.getValueList().addAll(0, refreshValueList);
        }

        String conditionSql = criteriaParser.parseCondition(condition);

        conditionSql = SqlParserUtil.mapper(conditionSql, parsed);

        sb.append(conditionSql);

        String sql = sb.toString();

        if (sql.contains("SET  WHERE"))
            throw new SqlBuildException(sql);

        return sql;
    }


    protected static String buildIn(String sql, String mapper, BeanElement be, List<? extends Object> inList) {

        StringBuilder sb = new StringBuilder();
        sb.append(sql).append(SqlScript.WHERE);
        sb.append(mapper).append(SqlScript.IN).append(SqlScript.LEFT_PARENTTHESIS);//" IN ("

        Class<?> keyType = be.getMethod.getReturnType();
        boolean isNumber = (keyType == long.class || keyType == int.class || keyType == Long.class
                || keyType == Integer.class);

        int size = inList.size();
        if (isNumber) {
            for (int i = 0; i < size; i++) {
                Object id = inList.get(i);
                if (id == null)
                    continue;
                sb.append(id);
                if (i < size - 1) {
                    sb.append(SqlScript.COMMA);
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                Object id = inList.get(i);
                if (id == null || StringUtil.isNullOrEmpty(id.toString()))
                    continue;
                sb.append(SqlScript.SINGLE_QUOTES).append(id).append(SqlScript.SINGLE_QUOTES);
                if (i < size - 1) {
                    sb.append(SqlScript.COMMA);
                }
            }
        }

        sb.append(SqlScript.RIGHT_PARENTTHESIS);

        return sb.toString();
    }

    protected static SqlParsed fromCriteria(Criteria criteria, CriteriaParser criteriaParser, Dialect dialect) {
        SqlParsed sqlParsed = criteriaParser.parse(criteria);
        String sql = sqlParsed.getSql().toString();

        int page = criteria.getPage();
        int rows = criteria.getRows();

        int start = (page - 1) * rows;

        sql = dialect.match(sql, start, rows);

        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        sqlParsed.setSql(sb);
        DataObjectConverter.log(criteria.getValueList());

        return sqlParsed;
    }

    protected static String filter(String sql) {
        sql = sql.replace("drop", SqlScript.SPACE).replace("delete", SqlScript.SPACE).replace("insert", SqlScript.SPACE).replace(";", SqlScript.SPACE); // 手动拼接SQL,
        return sql;
    }
}
