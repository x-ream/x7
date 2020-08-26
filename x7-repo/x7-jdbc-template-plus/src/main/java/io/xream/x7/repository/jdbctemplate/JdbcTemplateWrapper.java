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
package io.xream.x7.repository.jdbctemplate;

import io.xream.sqli.api.Dialect;
import io.xream.sqli.api.JdbcWrapper;
import io.xream.sqli.api.RowHandler;
import io.xream.sqli.builder.Criteria;
import io.xream.sqli.exception.ExceptionTranslator;
import io.xream.sqli.parser.Parsed;
import io.xream.sqli.parser.Parser;
import io.xream.sqli.util.JsonStyleMapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * @Author Sim
 */
public class JdbcTemplateWrapper implements JdbcWrapper {

    private final Logger logger = LoggerFactory.getLogger(JdbcWrapper.class);

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> boolean createBatch(Class<T> clzz, String sql, Collection<T> objList, int batchSize, Dialect dialect) {
        Parsed parsed = Parser.get(clzz);
        this.jdbcTemplate.batchUpdate(sql, objList, batchSize, (pstmt, o) -> {
            List<Object> valueList = DataObjectConverter.objectToListForCreate(o, parsed.getBeanElementList(), dialect);
            int i = 1;
            for (Object value : valueList) {
                pstmt.setObject(i++, value);
            }
        });
        return true;
    }

    @Override
    public boolean create(boolean isAutoIncreaseId, String sql, List<Object> valueList) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (isAutoIncreaseId) {

            this.jdbcTemplate.update(connection -> {
                PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                int i = 1;
                for (Object value : valueList) {
                    pstmt.setObject(i++, value);
                }
                return pstmt;
            }, keyHolder);

        } else {
            this.jdbcTemplate.update(connection -> {
                PreparedStatement pstmt = connection.prepareStatement(sql);
                int i = 1;
                for (Object value : valueList) {
                    pstmt.setObject(i++, value);
                }
                return pstmt;
            });
        }

        if (isAutoIncreaseId) {
            return keyHolder.getKey().longValue() > 0;
        } else {
            return true;
        }
    }

    @Override
    public boolean createOrReplace(String sql, List<Object> valueList) {
        this.jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            int i = 1;
            for (Object value : valueList) {
                pstmt.setObject(i++, value);
            }
            return pstmt;
        });

        return true;
    }

    @Override
    public boolean refresh(String sql, Object[] valueArr) {
        if (valueArr == null)
            return jdbcTemplate.update(sql) > 0;
        return jdbcTemplate.update(sql, valueArr) > 0;
    }

    @Override
    public boolean remove(String sql, Object id) {
        return this.jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public boolean execute(String sql) {
        this.jdbcTemplate.execute(sql);
        return true;
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> clz, Collection<Object> list, Dialect dialect) {

        Parsed parsed = Parser.get(clz);
        final ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();
        final RowMapper<T> rowMapper = (resultSet, i) -> {

            Map<String,Object> map = columnMapRowMapper.mapRow(resultSet,i);
            try {
                T t = clz.newInstance();
                DataObjectConverter.initObj(t, map, parsed.getBeanElementList(),dialect);
                return t;
            }catch (Exception e) {
                throw ExceptionTranslator.onQuery(e, logger);
            }

        };

        if (list == null || list.isEmpty()) {
            return jdbcTemplate.query(sql, rowMapper);
        } else {
            Object[] arr = dialect.toArr(list);
            return jdbcTemplate.query(sql, arr, rowMapper);
        }

    }

    @Override
    public <K> List<K> queryForPlainValueList(Class<K> clzz, String sql, Collection<Object> valueList, Dialect dialect) {

        if (valueList == null || valueList.isEmpty()) {
            return this.jdbcTemplate.query(sql, new SingleColumnRowMapper<>(clzz));
        }else {
            Object[] arr = dialect.toArr(valueList);
            return this.jdbcTemplate.query(sql, arr,
                    new SingleColumnRowMapper<>(clzz));
        }
    }

    @Override
    public List<Map<String, Object>> queryForMapList(String sql, Criteria.ResultMapCriteria resultMapped, Dialect dialect) {

        List<Map<String, Object>> propertyMapList = queryForMapList0(sql,resultMapped,dialect,jdbcTemplate);
        if (resultMapped.isResultWithDottedKey())
            return propertyMapList;

        if (!propertyMapList.isEmpty())
            return JsonStyleMapUtil.toJsonableMapList(propertyMapList);

        return propertyMapList;
    }

    private List<Map<String, Object>> queryForMapList0(String sql, Criteria.ResultMapCriteria resultMapped, Dialect dialect, JdbcTemplate jdbcTemplate) {

        final ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();
        final RowMapper<Map<String,Object>> rowMapper = (resultSet, i) -> {

            Map<String,Object> map = columnMapRowMapper.mapRow(resultSet,i);
            try {
                return DataObjectConverter.dataToPropertyObjectMap(resultMapped.getClz(), map,resultMapped,dialect);
            }catch (Exception e) {
                throw ExceptionTranslator.onQuery(e, logger);
            }
        };

        Collection<Object> list = resultMapped.getValueList();

        if (list == null || list.isEmpty()) {
            return jdbcTemplate.query(sql, rowMapper);
        } else {
            Object[] arr = dialect.toArr(list);
            return jdbcTemplate.query(sql, arr, rowMapper);
        }
    }

    @Override
    public <T> void queryForMapToHandle(Class clzz, String sql, Collection<Object> valueList, Dialect dialect, Criteria.ResultMapCriteria ResultMapCriteria, RowHandler<T> handler) {

        Parsed parsed = Parser.get(clzz);
        RowMapper<Map<String, Object>> rowMapper = new ColumnMapRowMapper();

        jdbcTemplate.query(connection -> {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql,
                            ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(50);
            try {
                preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
            }catch (SQLException e){
            }

            if (valueList != null) {
                int i = 1;
                for (Object obj : valueList) {
                    preparedStatement.setObject(i++, obj);
                }
            }

            return preparedStatement;
        }, resultSet -> {

            Map<String, Object> dataMap = rowMapper.mapRow(resultSet, 0);

            T t = null;
            if (ResultMapCriteria == null) {
                try {
                    t = (T) clzz.newInstance();
                    DataObjectConverter.initObj(t, dataMap, parsed.getBeanElementList(),dialect);
                } catch (Exception e) {
                    throw ExceptionTranslator.onQuery(e, logger);
                }
            } else {
                Map<String, Object> objectMap = DataObjectConverter.dataToPropertyObjectMap(clzz, dataMap, ResultMapCriteria, dialect);

                if(!ResultMapCriteria.isResultWithDottedKey()){
                    objectMap = JsonStyleMapUtil.toJsonableMap(objectMap);
                }
                t = (T) objectMap;
            }
            if (t != null) {
                handler.handle(t);
            }

        });

    }

}
