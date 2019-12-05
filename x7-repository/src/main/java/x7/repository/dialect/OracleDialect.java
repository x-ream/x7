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
package x7.repository.dialect;

import x7.core.bean.*;
import x7.core.util.JsonX;
import x7.core.util.StringUtil;
import x7.repository.SqlParsed;
import x7.repository.mapper.Mapper;

import java.io.Reader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class OracleDialect implements Mapper.Dialect {

    private final String ALIA_NAME = "AAA";

    private Map<String, String> map = new HashMap<String, String>() {
        {
            put(DATE, "date");
            put(BYTE, "number(3, 0)");
            put(INT, "number(10, 0)");
            put(LONG, "number(18, 0)");
            put(BIG, "number(19, 2)");
            put(STRING, "varchar2");
            put(TEXT, "clob");
            put(LONG_TEXT, "clob");
            put(INCREAMENT, "");
            put(ENGINE, "");
        }

    };

    private final static String ORACLE_PAGINATION = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM ( ${SQL} ) A   WHERE ROWNUM <= ${END}  )  WHERE RN > ${BEGIN} ";
    private final static String ORACLE_PAGINATION_REGX_SQL = "${SQL}";
    private final static String ORACLE_PAGINATION_REGX_BEGIN = "${BEGIN}";
    private final static String ORACLE_PAGINATION_REGX_END = "${END}";


    public String match(SqlParsed sqlParsed, long start, long rows) {


        return null;
    }

    public String match(String sql, long start, long rows) {

        if (rows > 0)
            return ORACLE_PAGINATION.replace(ORACLE_PAGINATION_REGX_END, String.valueOf(start + rows))
                    .replace(ORACLE_PAGINATION_REGX_BEGIN, String.valueOf(start)).replace(ORACLE_PAGINATION_REGX_SQL, sql);
        return sql;

    }

    public String match(String sql, String sqlType) {
        String dateV = map.get(DATE);
        String byteV = map.get(BYTE);
        String intV = map.get(INT);
        String longV = map.get(LONG);
        String bigV = map.get(BIG);
        String textV = map.get(TEXT);
        String longTextV = map.get(LONG_TEXT);
        String stringV = map.get(STRING);
        String increamentV = map.get(INCREAMENT);
        String engineV = map.get(ENGINE);

        return sql.replace(DATE.trim(), dateV).replace(BYTE.trim(), byteV).replace(INT.trim(), intV)
                .replace(LONG.trim(), longV).replace(BIG.trim(), bigV).replace(TEXT.trim(), textV)
                .replace(LONG_TEXT.trim(), longTextV).replace(STRING.trim(), stringV)
                .replace(INCREAMENT.trim(), increamentV).replace(ENGINE.trim(), engineV);
    }

    private Object getObject(final String mapper, ResultSet rs, BeanElement element) throws Exception {

        Object obj = null;
        Class ec = element.clz;

        if (element.isJson) {
            obj = rs.getObject(mapper);
            if (Objects.isNull(obj))
                return null;

            String str = null;
            if (obj instanceof String) {
                str = obj.toString();
            } else if (obj instanceof oracle.sql.NCLOB) {

                oracle.sql.NCLOB clob = (oracle.sql.NCLOB) obj;

                Reader reader = clob.getCharacterStream();

                char[] charArr = new char[(int) clob.length()];
                reader.read(charArr);
                reader.close();

                str = new String(charArr);//FIXME UIF-8 ?
            }
            if (StringUtil.isNullOrEmpty(str))
                return null;

            str = str.trim();

            if (!(str.startsWith("{") || str.startsWith("[")))
                return str;
            if (ec == List.class) {
                Class geneType = element.geneType;
                return JsonX.toList(str, geneType);
            } else if (ec == Map.class) {
                return JsonX.toMap(str);
            } else {
                return JsonX.toObject(str, ec);
            }
        }


        obj = rs.getObject(mapper);

        if (obj == null)
            return null;

        if (obj instanceof BigDecimal) {

            BigDecimal bg = (BigDecimal) obj;
            if (ec == BigDecimal.class) {
                return bg;
            } else if (ec == int.class || ec == Integer.class) {
                return bg.intValue();
            } else if (ec == long.class || ec == Long.class) {
                return bg.longValue();
            } else if (ec == double.class || ec == Double.class) {
                return bg.doubleValue();
            } else if (ec == float.class || ec == Float.class) {
                return bg.floatValue();
            } else if (ec == boolean.class || ec == Boolean.class) {
                int i = bg.intValue();
                return i == 0 ? false : true;
            } else if (ec == Date.class) {
                long l = bg.longValue();
                return new Date(l);
            } else if (ec == java.sql.Date.class) {
                long l = bg.longValue();
                return new java.sql.Date(l);
            } else if (ec == Timestamp.class) {
                long l = bg.longValue();
                return new Timestamp(l);
            } else if (ec == byte.class || ec == Byte.class) {
                return bg.byteValue();
            }

        } else if (obj instanceof Timestamp && ec == Date.class) {
            Timestamp ts = (Timestamp) obj;
            return new Date(ts.getTime());
        }
        if (ec.isEnum()) {
            return Enum.valueOf(ec, obj.toString());
        }

        return obj;

    }

    @Override
    public Object mappedResult(String property, String mapper, Map<String,String> aliaMap, Map<String,String> resultAliaMap, ResultSet rs) throws Exception {

        if (mapper == null)
            throw new RuntimeException("Result key is empty?");

        if (property.contains(".")) {
            String[] arr = property.split("\\.");
            String alia = arr[0];
            String p = arr[1];
            String clzName = aliaMap.get(alia);
            if (StringUtil.isNullOrEmpty(clzName)){
                clzName = alia;
            }
            Parsed parsed = Parser.get(clzName);
            BeanElement element = parsed.getElement(p);

            if (mapper.contains(SqlScript.KEY_SQL)) {
                mapper = mapper.replace(SqlScript.KEY_SQL, SqlScript.NONE);
            }

            String m = resultAliaMap.get(mapper);
            mapper = (m == null ? mapper : m);

            if (element == null) {
                return rs.getObject(mapper);
            }

            return getObject(mapper, rs, element);
        } else {
            if (mapper.contains(SqlScript.KEY_SQL)) {
                mapper = mapper.replace(SqlScript.KEY_SQL, SqlScript.NONE);
            }
            return rs.getObject(mapper);
        }

    }

    @Override
    public <T> void initObj(T obj, ResultSet rs, BeanElement tempEle, List<BeanElement> eles) throws Exception {


        for (BeanElement ele : eles) {

            Method method = ele.setMethod;
            String mapper = ele.getMapper();


            Object value = getObject(mapper, rs, ele);
            if (value != null) {
                method.invoke(obj, value);
            }

        }
    }

    @Override
    public String filterResultKey(String mapper, Criteria.ResultMappedCriteria criteria) {
        Map<String,String> aliaMap = criteria.getResultAliaMap();
        String alian = ALIA_NAME + aliaMap.size();
        aliaMap.put(mapper, alian);
        String target = mapper + SqlScript.AS + alian;
        return target;
    }


    public void setJSON(int i, String str, PreparedStatement pstmt) throws Exception {

//        Reader reader = new StringReader(str);
//        pstmt.setNClob(i, reader);
//        reader.close();//FIXME ?

        pstmt.setObject(i, str);

    }

    public void setObject(int i, Object obj, PreparedStatement pstm) throws Exception {
        if (obj instanceof Reader) {
            Reader reader = (Reader) obj;
            pstm.setNClob(i, reader);
        } else {
            pstm.setObject(i, obj);
        }
    }

    public Object filterValue(Object value) {
        if (value instanceof String) {
            String str = (String) value;
            value = str.replace("<", "&lt").replace(">", "&gt");
        } else if (value instanceof Date) {
            Date date = (Date) value;
            Timestamp timestamp = new Timestamp(date.getTime());
            return timestamp;
        } else if (value instanceof Boolean) {
            Boolean b = (Boolean) value;
            return b.booleanValue() == true ? 1 : 0;
        }
        return value;
    }

}
