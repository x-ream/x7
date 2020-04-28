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
import io.xream.x7.common.bean.condition.InCondition;
import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.common.repository.X;
import io.xream.x7.common.util.BeanMapUtil;
import io.xream.x7.common.util.LoggerProxy;
import io.xream.x7.common.util.StringUtil;
import io.xream.x7.common.web.Page;
import io.xream.x7.repository.CriteriaParser;
import io.xream.x7.repository.KeyOne;
import io.xream.x7.repository.SqlParsed;
import io.xream.x7.repository.exception.TooManyResultsException;
import io.xream.x7.repository.mapper.DataObjectConverter;
import io.xream.x7.repository.mapper.Dialect;
import io.xream.x7.repository.mapper.Mapper;
import io.xream.x7.repository.mapper.MapperFactory;
import io.xream.x7.repository.util.ResultSortUtil;
import io.xream.x7.repository.util.SqlParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * @author Sim
 */
public class DaoImpl implements Dao {

    @Autowired
    private CriteriaParser criteriaParser;
    @Autowired
    private Dialect dialect;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Logger logger = LoggerFactory.getLogger(DaoImpl.class);

    @Override
    public boolean createBatch(List<? extends Object> objList) {

        if (objList.isEmpty())
            return false;
        Object obj = objList.get(0);
        Class clz = obj.getClass();
        String sql = MapperFactory.getSql(clz, Mapper.CREATE);

        LoggerProxy.debug(clz, sql);

        Parsed parsed = Parser.get(clz);
        final int batchSize = 500;
        try {

            this.jdbcTemplate.batchUpdate(sql, objList, batchSize, (pstmt, o) -> {
                List<Object> valueList = DataObjectConverter.objectToListForCreate(o, parsed.getBeanElementList(), dialect);
                int i = 1;
                for (Object value : valueList) {
                    pstmt.setObject(i++, value);
                }
            });

        } catch (Exception e) {
            throw DaoExceptionTranslator.onRollback(obj, e, logger);
        }

        return true;
    }

    @Override
    public <T> boolean remove(KeyOne<T> keyOne) {

        Class clz = keyOne.getClzz();
        String sql = MapperFactory.getSql(clz, Mapper.REMOVE);

        LoggerProxy.debug(clz, keyOne.get());
        LoggerProxy.debug(clz, sql);

        return this.jdbcTemplate.update(sql, keyOne.get()) > 0;
    }

    @Override
    public long create(Object obj) {

        Class clz = obj.getClass();

        try {
            String sql = MapperFactory.getSql(clz, Mapper.CREATE);

            Parsed parsed = Parser.get(clz);

            Long keyOneValue = parsed.tryToGetLongKey(obj);
            boolean isAutoIncreaseId = parsed.isAutoIncreaseId(keyOneValue);

            List<Object> valueList = DataObjectConverter.objectToListForCreate(obj, parsed.getBeanElementList(), dialect);

            LoggerProxy.debug(clz, valueList);
            LoggerProxy.debug(clz, sql);

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
                return keyHolder.getKey().longValue();
            } else {
                return keyOneValue;
            }

        } catch (Exception e) {
            throw DaoExceptionTranslator.onRollback(obj, e, logger);
        }

    }

    @Override
    public boolean createOrReplace(Object obj) {

        Class clz = obj.getClass();

        try {
            String createSql = MapperFactory.getSql(clz, Mapper.CREATE);
            final String sql = this.dialect.createOrReplaceSql(createSql);

            Parsed parsed = Parser.get(clz);
            List<Object> valueList = DataObjectConverter.objectToListForCreate(obj, parsed.getBeanElementList(), dialect);

            LoggerProxy.debug(clz, valueList);
            LoggerProxy.debug(clz, sql);

            this.jdbcTemplate.update(connection -> {
                PreparedStatement pstmt = connection.prepareStatement(sql);
                int i = 1;
                for (Object value : valueList) {
                    pstmt.setObject(i++, value);
                }
                return pstmt;
            });

            return true;

        } catch (Exception e) {
            throw DaoExceptionTranslator.onRollback(obj, e, logger);
        }
    }


    @Override
    public List<Map<String, Object>> list(Class clz, String sql, List<Object> conditionList) {

        sql = SqlUtil.filter(sql);
        Parsed parsed = Parser.get(clz);
        sql = SqlParserUtil.mapperForManu(sql, parsed);

        LoggerProxy.debug(clz, sql);

        return queryForList(sql, clz, conditionList, this.dialect, jdbcTemplate);
    }


    @Override
    public <T> T get(KeyOne<T> keyOne) {

        Class clz = keyOne.getClzz();
        String sql = MapperFactory.getSql(clz, Mapper.GET_ONE);

        LoggerProxy.debug(clz, sql);

        List<T> list = this.queryForList(sql, keyOne.getClzz(), Arrays.asList(keyOne.get()), this.dialect, this.jdbcTemplate);

        if (list.isEmpty())
            return null;

        return list.get(0);
    }


    @Override
    public <T> List<T> list(Object conditionObj) {

        Class clz = conditionObj.getClass();
        String sql = MapperFactory.getSql(clz, Mapper.LOAD);
        Parsed parsed = Parser.get(clz);

        Map<String, Object> queryMap = DataObjectConverter.objectToMapForQuery(parsed, conditionObj);
        sql = SqlUtil.concat(parsed, sql, queryMap);
        LoggerProxy.debug(clz, sql);

        return queryForList(sql, clz, queryMap.values(), this.dialect, jdbcTemplate);

    }

    @Override
    public <T> List<T> list(Criteria criteria) {

        Class clz = criteria.getClz();
        SqlParsed sqlParsed = SqlUtil.fromCriteria(criteria, criteriaParser, dialect);
        String sql = sqlParsed.getSql().toString();
        LoggerProxy.debug(clz, sql);

        List<Object> valueList = criteria.getValueList();
        List<T> list = queryForList(sql, clz, valueList, this.dialect, jdbcTemplate);
        ResultSortUtil.sort(list, criteria, Parser.get(clz));
        return list;
    }

    @Override
    public <T> Page<T> find(Criteria criteria) {

        Class clz = criteria.getClz();
        SqlParsed sqlParsed = SqlUtil.fromCriteria(criteria, criteriaParser, dialect);
        String sql = sqlParsed.getSql().toString();

        LoggerProxy.debug(clz, sql);

        List<Object> valueList = criteria.getValueList();
        List<T> list = queryForList(sql, clz, valueList, this.dialect, jdbcTemplate);
        Parsed parsed = Parser.get(clz);
        ResultSortUtil.sort(list, criteria, parsed);

        Page<T> pagination = PageBuilder.build(criteria, list, () -> getCount(clz, sqlParsed.getCountSql(), valueList));

        return pagination;
    }


    /**
     * getCount
     *
     * @param sql
     * @param list
     * @return
     */
    private long getCount(Class clz, String sql, Collection<Object> list) {
        LoggerProxy.debug(clz, sql);

        Object obj = this.queryForMapList(sql, list, dialect, jdbcTemplate).get(0).get("count");
        return Long.valueOf(obj.toString());
    }


    /**
     * 没有特殊需求，请不要调用此代码
     *
     * @param obj
     * @param sql
     */
    @Deprecated
    @Override
    public boolean execute(Object obj, String sql) {

        Class clz = obj.getClass();
        Parsed parsed = Parser.get(obj.getClass());

        sql = SqlUtil.filter(sql);
        sql = SqlParserUtil.mapperForManu(sql, parsed);

        LoggerProxy.debug(clz, sql);

        this.jdbcTemplate.execute(sql);

        return true;
    }


    @Override
    public boolean refreshByCondition(RefreshCondition refreshCondition) {

        Class clz = refreshCondition.getClz();
        Parsed parsed = Parser.get(clz);
        String sql = SqlUtil.buildRefresh(parsed, refreshCondition, this.criteriaParser);
        List<Object> valueList = refreshCondition.getValueList();

        LoggerProxy.debug(clz, valueList);
        LoggerProxy.debug(clz, sql);

        return update(sql, valueList, dialect, jdbcTemplate);
    }

    @Override
    public <T> boolean refresh(T t) {

        Class clz = t.getClass();
        Object[] arr = SqlUtil.refresh(t,clz);

        String sql = (String)arr[0];
        Collection<Object> valueList = (Collection<Object>)arr[1];
        LoggerProxy.debug(clz, valueList);
        LoggerProxy.debug(clz, sql);

        return update(sql,valueList,dialect,jdbcTemplate);
    }


    @Override
    public <T> List<T> in(InCondition inCondition) {

        Class<T> clz = inCondition.getClz();
        Parsed parsed = Parser.get(clz);

        String inProperty = inCondition.getProperty();
        if (StringUtil.isNullOrEmpty(inProperty)) {
            inProperty = parsed.getKey(X.KEY_ONE);
        }

        BeanElement be = parsed.getElementExisted(inProperty);

        String sql = MapperFactory.getSql(clz, Mapper.LOAD);
        String mapper = parsed.getMapper(inProperty);
        List<? extends Object> inList = inCondition.getInList();

        sql = SqlUtil.buildIn(sql, mapper, be, inList);

        LoggerProxy.debug(clz, sql);

        return queryForList(sql, clz, null, this.dialect, jdbcTemplate);
    }

    @Override
    public Page<Map<String, Object>> find(Criteria.ResultMappedCriteria resultMapped) {

        Class clz = resultMapped.getClz();
        SqlParsed sqlParsed = SqlUtil.fromCriteria(resultMapped, criteriaParser, dialect);
        String sql = sqlParsed.getSql().toString();

        LoggerProxy.debug(clz, sql);

        List<Map<String, Object>> list = queryForMapList(sql, resultMapped, this.dialect, jdbcTemplate);

        Page<Map<String, Object>> pagination = PageBuilder.build(resultMapped, list, () -> getCount(clz, sqlParsed.getCountSql(), resultMapped.getValueList()));

        return pagination;
    }

    @Override
    public List<Map<String, Object>> list(Criteria.ResultMappedCriteria resultMapped) {

        Class clz = resultMapped.getClz();
        SqlParsed sqlParsed = SqlUtil.fromCriteria(resultMapped, criteriaParser, dialect);
        String sql = sqlParsed.getSql().toString();

        LoggerProxy.debug(clz, sql);

        return queryForMapList(sql, resultMapped, this.dialect, jdbcTemplate);
    }


    @Override
    public <T> T getOne(T conditionObj) {

        Class clz = conditionObj.getClass();
        String sql = MapperFactory.getSql(clz, Mapper.LOAD);
        Parsed parsed = Parser.get(clz);

        Map<String, Object> queryMap = DataObjectConverter.objectToMapForQuery(parsed, conditionObj);
        sql = SqlUtil.concat(parsed, sql, queryMap);
        sql = SqlUtil.paged(sql, 1, 1, this.dialect);

        LoggerProxy.debug(clz, sql);

        if (queryMap.isEmpty())
            throw new IllegalArgumentException("API of getOne(T) can't accept blank object: " + conditionObj);

        List<T> list = queryForList(sql, clz, queryMap.values(), this.dialect, jdbcTemplate);

        if (list.isEmpty())
            return null;
        if (list.size() > 1)
            throw new TooManyResultsException("Expected one result (or null) to be returned by API of getOne(T), but found: " + list.size());
        return list.get(0);
    }

    @Override
    public void findToHandle(Criteria.ResultMappedCriteria resultMapped, RowHandler<Map<String,Object>> handler) {

        Class clz = resultMapped.getClz();
        SqlParsed sqlParsed = SqlUtil.fromCriteria(resultMapped, criteriaParser, dialect);
        String sql = sqlParsed.getSql().toString();
        LoggerProxy.debug(clz, sql);

        List<Object> valueList = resultMapped.getValueList();

        queryForMapToHandle(clz, sql, valueList, dialect, jdbcTemplate, resultMapped, handler);
    }

    @Override
    public <T> void findToHandle(Criteria criteria, RowHandler<T> handler) {

        Class clz = criteria.getClz();
        SqlParsed sqlParsed = SqlUtil.fromCriteria(criteria, criteriaParser, dialect);
        String sql = sqlParsed.getSql().toString();
        LoggerProxy.debug(clz, sql);

        List<Object> valueList = criteria.getValueList();

        queryForMapToHandle(clz, sql, valueList, dialect, jdbcTemplate, null, handler);
    }

    private <T> void queryForMapToHandle(Class clzz, String sql, Collection<Object> valueList, Dialect dialect, JdbcTemplate jdbcTemplate, Criteria.ResultMappedCriteria resultMappedCriteria, RowHandler<T> handler) {

        Parsed parsed = Parser.get(clzz);
        RowMapper<Map<String, Object>> rowMapper = new ColumnMapRowMapper();

        jdbcTemplate.query(connection -> {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql,
                            ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(50);
            preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);

            if (valueList != null) {
                int i = 1;
                for (Object obj : valueList) {
                    preparedStatement.setObject(i++, obj);
                }
            }

            return preparedStatement;
        }, resultSet -> {

            Map<String, Object> dataMap = rowMapper.mapRow(resultSet, 0);

            Map<String, Object> objectMap = DataObjectConverter.dataToPropertyObjectMap(clzz, dataMap, resultMappedCriteria, dialect);

            T t = null;
            if (resultMappedCriteria == null) {
                try {
                    t = (T) clzz.newInstance();
                    DataObjectConverter.initObj(t, objectMap, parsed.getBeanElementList());
                } catch (Exception e) {
                    throw DaoExceptionTranslator.onQuery(e, logger);
                }
            } else {
                objectMap = BeanMapUtil.toJsonableMap(objectMap);
                t = (T) objectMap;
            }
            if (t != null) {
                handler.handle(t);
            }

        });


    }


    private boolean update(String sql, Collection<Object> list, Dialect dialect, JdbcTemplate jdbcTemplate) {
        try {
            Object[] arr = dialect.toArr(list);
            if (arr == null)
                return jdbcTemplate.update(sql) > 0;
            return jdbcTemplate.update(sql, arr) > 0;
        } catch (Exception e) {
            throw DaoExceptionTranslator.onRollback(null, e, logger);
        }
    }

    private List<Map<String, Object>> queryForMapList(String sql, Collection<Object> list, Dialect dialect, JdbcTemplate jdbcTemplate) {
        if (list == null || list.isEmpty()) {
            return jdbcTemplate.queryForList(sql);
        } else {
            Object[] arr = dialect.toArr(list);
            return jdbcTemplate.queryForList(sql, arr);
        }
    }

    private <T> List<T> queryForList(String sql, Class<T> clz, Collection<Object> list, Dialect dialect, JdbcTemplate jdbcTemplate) {
        List<Map<String, Object>> dataMapList = this.queryForMapList(sql, list, dialect, jdbcTemplate);
        List<Map<String, Object>> propertyMapList = DataObjectConverter.dataToPropertyObjectMapList(clz, dataMapList, null, dialect);
        List<T> tList = new ArrayList<>();
        Parsed parsed = Parser.get(clz);
        try {
            for (Map<String, Object> map : propertyMapList) {
                T t = clz.newInstance();
                DataObjectConverter.initObj(t, map, parsed.getBeanElementList());
                tList.add(t);
            }
        } catch (Exception e) {
            throw DaoExceptionTranslator.onQuery(e, logger);
        }
        return tList;
    }


    private List<Map<String, Object>> queryForMapList(String sql, Criteria.ResultMappedCriteria resultMapped, Dialect dialect, JdbcTemplate jdbcTemplate) {

        List<Object> list = resultMapped.getValueList();
        List<Map<String, Object>> dataMapList = queryForMapList(sql, list, dialect, jdbcTemplate);
        List<Map<String, Object>> propertyMapList = DataObjectConverter.dataToPropertyObjectMapList(resultMapped.getClz(), dataMapList, resultMapped, dialect);

        if (!propertyMapList.isEmpty())
            return BeanMapUtil.toJsonableMapList(propertyMapList);

        return propertyMapList;
    }

}