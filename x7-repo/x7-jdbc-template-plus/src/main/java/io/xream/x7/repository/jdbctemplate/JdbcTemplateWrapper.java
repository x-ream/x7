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

import io.xream.sqli.api.*;
import io.xream.sqli.builder.Criteria;
import io.xream.sqli.exception.ExceptionTranslator;
import io.xream.sqli.parser.Parsed;
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
    public <T> boolean createBatch(Class<T> clzz, String sql, BatchObjectValues batchObjectValues, int batchSize, Dialect dialect) {
        this.jdbcTemplate.batchUpdate(sql, batchObjectValues.valuesList(), batchSize, (pstmt, o) -> {
            int i = 1;
            for (Object value : o) {
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
    public <T> List<T> queryForList(String sql, Collection<Object> list, Parsed parsed, Dialect dialect) {

        return  toObjectList(
                fixedObjectBuilder -> queryForList0(
                        sql,
                        list,
                        parsed,
                        dialect,
                        fixedObjectBuilder)) ;

    }

    private  <T> List<T> queryForList0(String sql, Collection<Object> list, Parsed parsed, Dialect dialect, ObjectFinder.FixedRowMapper fixedRowMapper) {

        final ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();
        final RowMapper<T> rowMapper = (resultSet, i) -> {

            Map<String, Object> map = columnMapRowMapper.mapRow(resultSet, i);
            try {
                Class<T> clz = parsed.getClz();
                T t = clz.newInstance();
                fixedRowMapper.mapRow(t,map,parsed.getBeanElementList(),dialect);
                return t;
            } catch (Exception e) {
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
        } else {
            Object[] arr = dialect.toArr(valueList);
            return this.jdbcTemplate.query(sql, arr,
                    new SingleColumnRowMapper<>(clzz));
        }
    }

    @Override
    public List<Map<String, Object>> queryForResultMapList(String sql, Criteria.ResultMapCriteria resultMapped, Dialect dialect) {

        return toResultMapList(
                resultMapped.isResultWithDottedKey(),
                fixedRowMapper -> queryForMapList0(
                        sql,
                        resultMapped.getClz(),
                        resultMapped.getValueList(),
                        dialect,
                        jdbcTemplate,
                        resultMapped,
                        fixedRowMapper));

    }

    private List<Map<String, Object>> queryForMapList0(String sql, Class clzz, Collection<Object> list, Dialect dialect, JdbcTemplate jdbcTemplate, Criteria.ResultMapCriteria resultMapped, ResultMapFinder.FixedRowMapper fixedRowMapper) {

        final ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();
        final RowMapper<Map<String, Object>> rowMapper = (resultSet, i) -> {

            Map<String, Object> map = columnMapRowMapper.mapRow(resultSet, i);
            try {
                return fixedRowMapper.mapRow(map, clzz, resultMapped, dialect);
            } catch (Exception e) {
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
    public <T> void queryForMapToHandle(String sql, Collection<Object> valueList, Dialect dialect, Criteria.ResultMapCriteria resultMapCriteria, Parsed orParsed, RowHandler<T> handler) {

        RowMapper<Map<String, Object>> rowMapper = new ColumnMapRowMapper();

        jdbcTemplate.query(connection -> {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql,
                            ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(50);
            try {
                preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
            } catch (SQLException e) {
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
            if (resultMapCriteria == null) {
                try {
                    Class<T> clzz = orParsed.getClz();
                    t = (T) clzz.newInstance();
                    toObject(t, dataMap, orParsed.getBeanElementList(), dialect);
                } catch (Exception e) {
                    throw ExceptionTranslator.onQuery(e, logger);
                }
            } else {
                 t = (T) toResultMap(resultMapCriteria,dialect,dataMap);
            }
            if (t != null) {
                handler.handle(t);
            }

        });

    }

}
