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
package x7.repository.mapper;

import x7.core.bean.BeanElement;
import x7.core.bean.Criteria;
import x7.repository.SqlParsed;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Mapper {

    String CREATE = "CREATE";
    String REFRESH = "REFRESH";
    String REMOVE = "REMOVE";
    String QUERY = "QUERY";
    String LOAD = "LOAD";
    String TAG = "TAG";
    String CREATE_TABLE = "CREATE_TABLE";


    interface Interpreter {

        String getTableSql(Class clz);

        String getRefreshSql(Class clz);

        String getQuerySql(Class clz);

        String getLoadSql(Class clz);

        String getCreateSql(Class clz);

        String getTagSql(Class clz);
    }

    static String getSqlTypeRegX(BeanElement be) {

        Class clz = be.clz;
        if (clz == Date.class || clz == java.sql.Date.class || clz == java.sql.Timestamp.class) {
            return Dialect.DATE;
        } else if (clz == String.class) {
            return Dialect.STRING;
        } else if (clz.isEnum()) {
            return Dialect.STRING;
        } else if (clz == int.class || clz == Integer.class) {
            return Dialect.INT;
        } else if (clz == long.class || clz == Long.class) {
            return Dialect.LONG;
        } else if (clz == double.class || clz == Double.class) {
            return Dialect.BIG;
        } else if (clz == float.class || clz == Float.class) {
            return Dialect.BIG;
        } else if (clz == BigDecimal.class) {
            return Dialect.BIG;
        } else if (clz == boolean.class || clz == Boolean.class) {
            return Dialect.BYTE;
        } else if (clz == short.class || clz == Short.class) {
            return Dialect.INT;
        } else if (clz == byte.class || clz == Byte.class) {
            return Dialect.BYTE;
        }
        return Dialect.TEXT;

    }

    interface Dialect {

        String DATE = " ${DATE}";
        String BYTE = " ${BYTE}";
        String INT = " ${INT}";
        String LONG = " ${LONG}";
        String BIG = " ${BIG}";
        String STRING = " ${STRING}";
        String TEXT = " ${TEXT}";
        String LONG_TEXT = " ${LONG_TEXT}";
        String INCREAMENT = " ${INCREAMENT}";
        String ENGINE = " ${ENGINE}";

        String match(SqlParsed sql, long start, long rows);
        String match(String sql, long start, long rows);

        String match(String sql, String sqlType);


        <T> void initObj(T obj, ResultSet rs, BeanElement tempEle, List<BeanElement> eles) throws Exception;

        void setObject(int i, Object obj, PreparedStatement pstm) throws Exception ;


        void setJSON(int i, String str, PreparedStatement pstmt) throws Exception ;

        Object mappedResult(String property, String mapper, Map<String,String> aliaMap,Map<String,String> resultAliaMap, ResultSet rs) throws Exception;

        Object filterValue(Object value);

        String filterResultKey(String mapper, Criteria.ResultMappedCriteria criteria);
    }
}
