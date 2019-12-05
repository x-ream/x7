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

import x7.core.bean.*;
import x7.core.bean.condition.RefreshCondition;
import x7.core.util.*;
import x7.repository.CriteriaParser;
import x7.repository.exception.PersistenceException;
import x7.repository.util.SqlParserUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.*;


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
            }catch (Exception ee){
                throw new RuntimeException(ExceptionUtil.getMessage(ee));
            }
        }
        try {
            Object value = method.invoke(obj);
            pstmt.setObject(i++, value);
        }catch (Exception e) {
            throw new PersistenceException(ExceptionUtil.getMessage(e));
        }
    }

    protected static void adpterSqlKey(PreparedStatement pstmt, Field keyOneF, Object obj, int i){
        try {
            pstmt.setObject(i++, obj);
        }catch (Exception e){
            throw new PersistenceException(ExceptionUtil.getMessage(e));
        }

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



    /**
     * 拼接SQL
     */
    protected static String concatRefresh(StringBuilder sb, Parsed parsed,
                                          RefreshCondition refreshCondition, CriteriaParser criteriaParser) {

        sb.append(SqlScript.SET);

        List<Criteria.X> refreshList = refreshCondition.getRefreshList();

        List<Object> refreshValueList = new ArrayList<>();

        boolean isNotFirst = false;
        for (Criteria.X x : refreshList) {


            if (x.getPredicate() == Predicate.X) {

                if (isNotFirst){
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

                    if (isNotFirst){
                        sb.append(SqlScript.COMMA).append(SqlScript.SPACE);
                    }

                    isNotFirst = true;

                    String sql = BeanUtilX.normalizeSql(key);
                    sql = SqlParserUtil.mapper(sql, parsed);
                    sb.append(sql);
                } else {

                    if (StringUtil.isNullOrEmpty(x.getValue().toString()) || BeanUtilX.isBaseType_0(key,x.getValue(),parsed)) {
                        continue;
                    }

                    if (isNotFirst){
                        sb.append(SqlScript.COMMA).append(SqlScript.SPACE);
                    }

                    isNotFirst = true;

                    String mapper = parsed.getMapper(key);
                    sb.append(mapper);
                    sb.append(SqlScript.EQ_PLACE_HOLDER);

                    BeanElement be = parsed.getElementMap().get(key);
                    if (be.clz == Date.class ) {
                        if (x.getValue() instanceof Long) {
                            x.setValue(new Date(((Long) x.getValue()).longValue()));
                        }
                    }else if (be.clz == Timestamp.class){
                        if (x.getValue() instanceof Long) {
                            x.setValue(new Timestamp(((Long) x.getValue()).longValue()));
                        }
                    }else if (be.isJson){
                        Object v = x.getValue();
                        if (v != null){
                            String str = JsonX.toJson(v);
                            x.setValue(str);
                        }
                    }

                }

                Object v = x.getValue();
                if (v instanceof Enum){
                    String name = ((Enum) v).name();
                    x.setValue(name);
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

        return sb.toString();
    }

    protected static void adpterRefreshCondition(PreparedStatement pstmt,
                                                 int i, CriteriaCondition condition) {

        if (Objects.nonNull(condition)) {
            for (Object v : condition.getValueList()) {
                setValue(i,pstmt,v);
                i++;
            }
        }
    }

    public static void setValue(int i, PreparedStatement pstmt, Object obj) {
        try {
            if (Objects.nonNull(obj) && obj.getClass().isEnum()) {
                pstmt.setObject(i, ((Enum)obj).name());
            } else {
                pstmt.setObject(i, obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }
    }

}
