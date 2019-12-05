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
import x7.repository.dao.SqlUtil;
import x7.repository.mapper.Mapper;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySqlDialect implements Mapper.Dialect {

    private Map<String, String> map = new HashMap<String, String>() {
        {

            put(DATE, "timestamp");
            put(BYTE, "tinyint(1)");
            put(INT, "int(11)");
            put(LONG, "bigint(13)");
            put(BIG, "decimal(15,2)");
            put(STRING, "varchar");
            put(TEXT, "text");
            put(LONG_TEXT, "longtext");
            put(INCREAMENT, "AUTO_INCREMENT");
            put(ENGINE, "ENGINE=InnoDB DEFAULT CHARSET=utf8");

        }

    };

    public String match(SqlParsed sqlParsed, long start, long rows) {


        return null;
    }

    public String match(String sql, long start, long rows) {

        if (rows == 0)
            return sql;
        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        sb.append(SqlScript.LIMIT).append(start).append(",").append(rows);
        return sb.toString();

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

        Class ec = element.clz;
        Object obj = rs.getObject(mapper);

        if (obj == null)
            return null;

        if (ec.isEnum()) {
            return Enum.valueOf(ec, obj.toString());
        } else if (element.isJson) {
            if (ec == List.class) {
                Class geneType = element.geneType;
                return JsonX.toList(obj.toString(), geneType);
            } else if (ec == Map.class) {
                return JsonX.toMap(obj);
            } else {
                return JsonX.toObject(obj.toString(), ec);
            }
        } else if (ec == BigDecimal.class) {
            return new BigDecimal(String.valueOf(obj));
        } else if (ec == double.class || ec == Double.class) {
            return Double.valueOf(obj.toString());
        }

        return obj;
    }

    @Override
    public Object mappedResult(String property, String mapper,Map<String, String> aliaMap,  Map<String, String> resultAliaMap, ResultSet rs) throws Exception {

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


            if (mapper.contains("`")) {
                mapper = mapper.replace("`", "");
            }

            if (element == null) {
                return rs.getObject(mapper);
            }

            return getObject(mapper, rs, element);
        } else {
            if (mapper.contains("`")) {
                mapper = mapper.replace("`", "");
            }
            return rs.getObject(mapper);
        }


    }

    @Override
    public <T> void initObj(T obj, ResultSet rs, BeanElement tempEle, List<BeanElement> eles) throws Exception {

        for (BeanElement ele : eles) {

            Method method = ele.setMethod;
            String mapper = ele.getMapper();

            if (mapper.contains("`")) {
                mapper = mapper.replace("`", "");
            }

            Object value = getObject(mapper, rs, ele);
            method.invoke(obj, value);

        }

    }


    public void setJSON(int i, String str, PreparedStatement pstmt) throws Exception {

        pstmt.setString(i, str);

    }

    public void setObject(int i, Object obj, PreparedStatement pstm) throws Exception {

//        pstm.setObject(i, obj);
        SqlUtil.setValue(i,pstm,obj);

    }

    public Object filterValue(Object value) {

        if (value instanceof String) {
            String str = (String) value;
            value = str.replace("<", "&lt").replace(">", "&gt");
        }
        return value;
    }


    @Override
    public String filterResultKey(String mapper, Criteria.ResultMappedCriteria criteria) {
        return mapper;
    }
}
