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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import x7.core.bean.BeanElement;
import x7.core.bean.Criteria;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.RefreshCondition;
import x7.core.config.ConfigAdapter;
import x7.core.repository.X;
import x7.core.util.BeanMapUtil;
import x7.core.util.ExceptionUtil;
import x7.core.util.StringUtil;
import x7.core.web.Page;
import x7.repository.CriteriaParser;
import x7.repository.KeyOne;
import x7.repository.SqlParsed;
import x7.repository.exception.QueryException;
import x7.repository.exception.RollbackException;
import x7.repository.mapper.DataObjectConverter;
import x7.repository.mapper.Dialect;
import x7.repository.mapper.Mapper;
import x7.repository.mapper.MapperFactory;
import x7.repository.util.ResultSortUtil;
import x7.repository.util.SqlParserUtil;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Sim
 */
public class DaoImpl implements Dao {

    private final static Logger logger = LoggerFactory.getLogger(Dao.class);

    @Autowired
    private CriteriaParser criteriaParser;
    @Autowired
    private Dialect dialect;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public boolean createBatch(List<? extends Object> objList) {

        if (objList.isEmpty())
            return false;
        Object obj = objList.get(0);
        Class clz = obj.getClass();
        String sql = MapperFactory.getSql(clz, Mapper.CREATE);

        if (ConfigAdapter.isIsShowSql())
            logger.info(sql);

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
            logger.info("Dao#createBatch : " + obj + ", Exception: " + ExceptionUtil.getMessage(e));
            throw new RollbackException(ExceptionUtil.getMessage(e) + ", while create " + obj);
        }

        return true;
    }

    @Override
    public <T> boolean remove(KeyOne<T> keyOne) {

        Class clz = keyOne.getClzz();
        String sql = MapperFactory.getSql(clz, Mapper.REMOVE);

        if (ConfigAdapter.isIsShowSql())
            logger.info(sql);

        return this.jdbcTemplate.update(sql, keyOne.get()) > 0;
    }

    @Override
    public long create(Object obj) {

        Class clz = obj.getClass();

        try {
            String sql = MapperFactory.getSql(clz, Mapper.CREATE);
            if (ConfigAdapter.isIsShowSql())
                logger.info(sql);

            Parsed parsed = Parser.get(clz);

            Long keyOneValue = parsed.tryToGetLongKey(obj);
            boolean isAutoIncreaseId = parsed.isAutoIncreaseId(keyOneValue);

            List<Object> valueList = DataObjectConverter.objectToListForCreate(obj, parsed.getBeanElementList(), dialect);
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
            logger.info("Dao#create : " + obj + ", Exception: " + ExceptionUtil.getMessage(e));
            throw new RollbackException(ExceptionUtil.getMessage(e) + ", while create " + obj);
        }

    }


    @Override
    public List<Map<String, Object>> list(Class clz, String sql, List<Object> conditionList) {

        sql = SqlUtil.filter(sql);
        Parsed parsed = Parser.get(clz);
        sql = SqlParserUtil.mapperForManu(sql, parsed);

        if (ConfigAdapter.isIsShowSql())
            logger.info(sql);

        return queryForList(sql, clz, conditionList, this.dialect, jdbcTemplate);
    }


    @Override
    public <T> List<T> list(Object conditionObj) {

        Class clz = conditionObj.getClass();
        String sql = MapperFactory.getSql(clz, Mapper.LOAD);
        Parsed parsed = Parser.get(clz);

        Map<String, Object> queryMap = DataObjectConverter.objectToMapForQuery(parsed, conditionObj);
        sql = SqlUtil.concat(parsed, sql, queryMap);
        if (ConfigAdapter.isIsShowSql())
            logger.info(sql);

        return queryForList(sql, clz, queryMap.values(), this.dialect, jdbcTemplate);

    }

    @Override
    public <T> List<T> list(Criteria criteria) {

        SqlParsed sqlParsed = SqlUtil.fromCriteria(criteria, criteriaParser, dialect);
        String sql = sqlParsed.getSql().toString();
        if (ConfigAdapter.isIsShowSql())
            logger.info(sql);

        Class clz = criteria.getClz();
        List<Object> valueList = criteria.getValueList();
        return queryForList(sql, clz, valueList, this.dialect, jdbcTemplate);
    }

    @Override
    public <T> Page<T> find(Criteria criteria) {

        SqlParsed sqlParsed = SqlUtil.fromCriteria(criteria, criteriaParser, dialect);
        String sql = sqlParsed.getSql().toString();
        if (ConfigAdapter.isIsShowSql())
            logger.info(sql);

        Class clz = criteria.getClz();
        List<Object> valueList = criteria.getValueList();
        List<T> list = queryForList(sql, clz, valueList, this.dialect, jdbcTemplate);
        Parsed parsed = Parser.get(clz);
        ResultSortUtil.sort(list, criteria, parsed);

        Page<T> pagination = PageBuilder.build(criteria, list, () -> getCount(sqlParsed.getCountSql(), valueList));

        return pagination;
    }


    /**
     * getCount
     *
     * @param sql
     * @param list
     * @return
     */
    private long getCount(String sql, Collection<Object> list) {
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

        Parsed parsed = Parser.get(obj.getClass());

        sql = SqlUtil.filter(sql);
        sql = SqlParserUtil.mapperForManu(sql, parsed);

        if (ConfigAdapter.isIsShowSql())
            logger.info(sql);

        this.jdbcTemplate.execute(sql);

        return true;
    }


    @Override
    public boolean refreshByCondition(RefreshCondition refreshCondition) {

        Class clz = refreshCondition.getClz();
        Parsed parsed = Parser.get(clz);
        String sql = SqlUtil.buildRefresh(parsed, refreshCondition, this.criteriaParser);

        if (ConfigAdapter.isIsShowSql())
            logger.info(sql);

        return update(sql, refreshCondition.getCondition().getValueList(), dialect, jdbcTemplate);
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
        if (ConfigAdapter.isIsShowSql())
            logger.info(sql);

        return queryForList(sql, clz, null, this.dialect, jdbcTemplate);
    }

    @Override
    public Page<Map<String, Object>> find(Criteria.ResultMappedCriteria resultMapped) {

        SqlParsed sqlParsed = SqlUtil.fromCriteria(resultMapped, criteriaParser, dialect);
        String sql = sqlParsed.getSql().toString();
        if (ConfigAdapter.isIsShowSql())
            logger.info(sql);

        List<Map<String, Object>> list = queryForMapList(sql, resultMapped, this.dialect, jdbcTemplate);

        Page<Map<String, Object>> pagination = PageBuilder.build(resultMapped, list, () -> getCount(sqlParsed.getCountSql(), resultMapped.getValueList()));

        return pagination;
    }

    @Override
    public List<Map<String, Object>> list(Criteria.ResultMappedCriteria resultMapped) {

        SqlParsed sqlParsed = SqlUtil.fromCriteria(resultMapped, criteriaParser, dialect);
        String sql = sqlParsed.getSql().toString();
        if (ConfigAdapter.isIsShowSql())
            logger.info(sql);

        return queryForMapList(sql, resultMapped, this.dialect, jdbcTemplate);
    }


    @Override
    public <T> T getOne(T conditionObj) {

        List<T> list = list(conditionObj);
        if (list.isEmpty())
            return null;
        return list.get(0);
    }


    private boolean update(String sql, Collection<Object> list, Dialect dialect, JdbcTemplate jdbcTemplate) {
        try {
            Object[] arr = dialect.toArr(list);
            if (arr == null)
                return jdbcTemplate.update(sql) > 0;
            return jdbcTemplate.update(sql, arr) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            String str = ExceptionUtil.getMessage(e);
            logger.info(str);
            throw new QueryException(str);
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
        List<Map<String, Object>> propertyMapList = DataObjectConverter.dataToPropertyObjectMap(clz, dataMapList, null, dialect);
        List<T> tList = new ArrayList<>();
        Parsed parsed = Parser.get(clz);
        try {
            for (Map<String, Object> map : propertyMapList) {
                T t = clz.newInstance();
                DataObjectConverter.initObj(t, map, parsed.getBeanElementList());
                tList.add(t);
            }
        } catch (Exception e) {
        }
        return tList;
    }


    private List<Map<String, Object>> queryForMapList(String sql, Criteria.ResultMappedCriteria resultMapped, Dialect dialect, JdbcTemplate jdbcTemplate) {

        List<Object> list = resultMapped.getValueList();
        List<Map<String, Object>> dataMapList = queryForMapList(sql, list, dialect, jdbcTemplate);
        List<Map<String, Object>> propertyMapList = DataObjectConverter.dataToPropertyObjectMap(resultMapped.getClz(), dataMapList, resultMapped, dialect);

        if (!propertyMapList.isEmpty()) {
            return BeanMapUtil.toJsonableMapList(propertyMapList);
        }

        return propertyMapList;
    }


}
