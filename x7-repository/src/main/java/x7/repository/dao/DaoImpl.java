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
import x7.core.bean.*;
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.RefreshCondition;
import x7.core.config.ConfigAdapter;
import x7.core.repository.X;
import x7.core.util.BeanMapUtil;
import x7.core.util.ExceptionUtil;
import x7.core.util.JsonX;
import x7.core.util.StringUtil;
import x7.core.web.Page;
import x7.repository.CriteriaParser;
import x7.repository.KeyOne;
import x7.repository.SqlParsed;
import x7.repository.exception.PersistenceException;
import x7.repository.exception.RollbackException;
import x7.repository.mapper.Mapper;
import x7.repository.mapper.MapperFactory;
import x7.repository.util.ResultSetUtil;
import x7.repository.util.ResultSortUtil;
import x7.repository.util.SqlParserUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * @author Sim
 */
public class DaoImpl implements Dao {


    private final static Logger logger = LoggerFactory.getLogger(Dao.class);

    public DaoImpl() {
    }

    @Autowired
    private CriteriaParser criteriaParser;

    @Autowired
    private Mapper.Dialect dialect;

    public void setDialect(Mapper.Dialect dialect) {
        this.dialect = dialect;
    }
    public void setCriteriaParser(CriteriaParser criteriaParser){
        this.criteriaParser = criteriaParser;
    }


    /**
     * 放回连接池,<br>
     * 连接池已经重写了关闭连接的方法
     */
    private static void close(Connection conn) {
        RcDataSourceUtil.releaseConnection(conn);
    }

    private static void close(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean createBatch(List<? extends Object> objList) {

        if (objList.isEmpty())
            return false;
        Object obj = objList.get(0);
        Class clz = obj.getClass();

        String sql = MapperFactory.getSql(clz, Mapper.CREATE);

        List<BeanElement> eles = MapperFactory.getElementList(clz);

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Parsed parsed = Parser.get(clz);

            Long keyOneValue = 0L;
            Field keyOneField = parsed.getKeyField(X.KEY_ONE);
            if (Objects.isNull(keyOneField))
                throw new PersistenceException("No setting of PrimaryKey by @X.Key");
            Class keyOneType = keyOneField.getType();
            if (keyOneType != String.class) {
                Object keyValue = keyOneField.get(obj);
                if (keyValue != null ) {
                    keyOneValue = Long.valueOf(keyValue.toString());
                }
            }

            conn = DataSourceUtil.getConnection();

            if (keyOneType != String.class && (keyOneValue == null || keyOneValue == 0)) {
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                pstmt = conn.prepareStatement(sql);
            }

            for (Object o : objList) {

                int i = 1;
                for (BeanElement ele : eles) {

                    Object value = ele.getMethod.invoke(o);
                    if (value == null) {
                        if (ele.clz.isEnum())
                            throw new PersistenceException(
                                    "ENUM CAN NOT NULL, property:" + clz.getName() + SqlScript.POINT + ele.getProperty());
                        if (ele.clz == Boolean.class || ele.clz == Integer.class || ele.clz == Long.class
                                || ele.clz == Double.class || ele.clz == Float.class || ele.clz == BigDecimal.class
                                || ele.clz == Byte.class || ele.clz == Short.class)
                            value = 0;
                        pstmt.setObject(i++, value);
                    } else {
                        if (ele.isJson) {
                            String str = JsonX.toJson(value);
                            this.dialect.setJSON(i++, str, pstmt);
                        } else if (ele.clz.isEnum()) {
                            String str = ((Enum)value).name();
                            pstmt.setObject(i++, str);
                        } else {
                            value = this.dialect.filterValue(value);
                            pstmt.setObject(i++, value);
                        }
                    }

                }

                pstmt.addBatch();

            }

            pstmt.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();

            throw new RollbackException("RollbackException: " + e.getMessage() + ", while create: " + obj);

        } finally {
            close(pstmt);
            DataSourceUtil.releaseConnection(conn);
        }

        return true;
    }


    protected <T> boolean remove(KeyOne<T> keyOne, Connection conn) {

        Class clz = keyOne.getClzz();

        String sql = MapperFactory.getSql(clz, Mapper.REMOVE);

        boolean flag = false;

        PreparedStatement pstmt = null;
        try {

            pstmt = conn.prepareStatement(sql);

            Parsed parsed = Parser.get(clz);

            int i = 1;

            Field keyOneField = parsed.getKeyField(X.KEY_ONE);
            if (Objects.isNull(keyOneField))
                throw new PersistenceException("No setting of PrimaryKey by @X.Key");
            SqlUtil.adpterSqlKey(pstmt, keyOneField, keyOne.get(), i);

            flag = pstmt.executeUpdate() == 0 ? false : true;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RollbackException("RollbackException: " + e.getMessage());

        } finally {
            close(pstmt);
            DataSourceUtil.releaseConnection(conn);
        }

        return flag;
    }

    protected long create(Object obj, Connection conn) {

        Class clz = obj.getClass();

        long id = -1;
        PreparedStatement pstmt = null;
        try {
            String sql = MapperFactory.getSql(clz, Mapper.CREATE);
            Parsed parsed = Parser.get(clz);
            List<BeanElement> eles = parsed.getBeanElementList();

            Long keyOneValue = 0L;
            Field keyOneField = parsed.getKeyField(X.KEY_ONE);
            if (Objects.isNull(keyOneField))
                throw new PersistenceException("No setting of PrimaryKey by @X.Key");
            Class keyOneType = keyOneField.getType();
            if (keyOneType == String.class) {
                keyOneValue = 1L;
            }else{
                Object keyValue = keyOneField.get(obj);
                if (keyValue != null) {
                    keyOneValue = Long.valueOf(keyValue.toString());
                }
            }

            /*
             * 返回自增键
             */
            if (keyOneType != String.class && (keyOneValue == null || keyOneValue == 0)) {
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                pstmt = conn.prepareStatement(sql);
            }

            int i = 1;
            for (BeanElement ele : eles) {
                Object value = ele.getMethod.invoke(obj);
                if (value == null) {
                    if (ele.clz.isEnum())
                        throw new PersistenceException(
                                "ENUM CAN NOT NULL, property:" + clz.getName() + SqlScript.POINT + ele.getProperty());
                    if (ele.clz == Boolean.class || ele.clz == Integer.class || ele.clz == Long.class
                            || ele.clz == Double.class || ele.clz == Float.class || ele.clz == BigDecimal.class
                            || ele.clz == Byte.class || ele.clz == Short.class)
                        value = 0;
                    pstmt.setObject(i++, value);
                } else {
                    if (ele.isJson) {
                        String str = JsonX.toJson(value);
                        this.dialect.setJSON(i++, str, pstmt);
                    } else if (ele.clz.isEnum()) {
                        String str = ((Enum)value).name();
                        pstmt.setObject(i++, str);
                    } else {
                        value = this.dialect.filterValue(value);
                        pstmt.setObject(i++, value);
                    }
                }
            }

            pstmt.execute();

            if (keyOneType != String.class && (keyOneValue == null || keyOneValue == 0)) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getLong(1);
                }
            } else {
                id = keyOneValue;
            }

        } catch (Exception e) {
            System.out.println("Exception occured, while create: " + obj);

            throw new RollbackException("RollbackException occoured: " + e.getMessage() + ", while create " + obj);

        } finally {
            close(pstmt);
            DataSourceUtil.releaseConnection(conn);
        }

        return id;
    }



    @Override
    public long create(Object obj) {

        Connection conn = null;
        try {
            conn = DataSourceUtil.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("NO CONNECTION");
        }
        return create(obj, conn);
    }


    @Override
    public <T> boolean remove(KeyOne<T> keyOne) {
        Connection conn = null;
        try {
            conn = DataSourceUtil.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("NO CONNECTION");
        }
        return remove(keyOne, conn);
    }


    protected List<Map<String, Object>> list(Class clz, String sql, List<Object> conditionList, Connection conn) {

        sql = sql.replace("drop", SqlScript.SPACE).replace("delete", SqlScript.SPACE).replace("insert", SqlScript.SPACE).replace(";", SqlScript.SPACE); // 手动拼接SQL,
        // 必须考虑应用代码的漏
        Parsed parsed = Parser.get(clz);

        sql = SqlParserUtil.mapper(sql, parsed);//FIXME 解析之后, 替换,拼接
        sql = SqlParserUtil.mapperForManu(sql, parsed);//FIXME 解析之后, 替换,拼接

        if (ConfigAdapter.isIsShowSql())
            System.out.println(sql);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        PreparedStatement pstmt = null;

        try {
            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(sql);

            int i = 1;
            if (conditionList != null) {
                for (Object value : conditionList) {
                    value = this.dialect.filterValue(value);
                    this.dialect.setObject(i++, value, pstmt);
                }
            }

            ResultSet rs = pstmt.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    Map<String, Object> mapR = new HashMap<String, Object>();
                    list.add(mapR);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int count = rsmd.getColumnCount();
                    for (i = 1; i <= count; i++) {
                        String key = rsmd.getColumnLabel(i);
                        String value = rs.getString(i);
                        String property = parsed.getProperty(key);
                        if (StringUtil.isNullOrEmpty(property)) {
                            property = key;
                        }
                        mapR.put(property, value);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(pstmt);
            close(conn);
        }

        return list;
    }

    public List<Map<String, Object>> list(Class clz, String sql, List<Object> conditionList) {
        Connection conn = RcDataSourceUtil.getConnection();
        return list(clz, sql, conditionList, conn);
    }

    protected <T> List<T> list(Object conditionObj, Connection conn) {

        Class clz = conditionObj.getClass();

        String sql = MapperFactory.getSql(clz, Mapper.LOAD);

        Parsed parsed = Parser.get(clz);

        Map<String, Object> queryMap = SqlParserUtil.getQueryMap(parsed, conditionObj);
        sql = SqlUtil.concat(parsed, sql, queryMap);

        if (ConfigAdapter.isIsShowSql())
            System.out.println(sql);

        List<T> list = new ArrayList<T>();

        PreparedStatement pstmt = null;
        BeanElement tempEle = null;
        try {
            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(sql);

            int i = 1;
            for (Object value : queryMap.values()) {
                value = this.dialect.filterValue(value);
                this.dialect.setObject(i++, value, pstmt);
            }

            ResultSet rs = pstmt.executeQuery();

            List<BeanElement> eles = parsed.getBeanElementList();
            if (rs != null) {
                while (rs.next()) {
                    T obj = (T) clz.newInstance();
                    list.add(obj);
                    initObj(obj, rs, tempEle, eles);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            String str = tempEle==null?"":(tempEle.property+"|"+tempEle.getMapper());
            throw new RollbackException(
                    "Exception occured by class = " + clz.getName() + ",column："+ str +", message: " + e.getMessage());
        } finally {
            close(pstmt);
            close(conn);
        }

        return list;
    }

    @Override
    public <T> List<T> list(Object conditionObj) {
        Connection conn = RcDataSourceUtil.getConnection();
        return list(conditionObj, conn);
    }

    protected <T> List<T> list(Criteria criteria, Connection conn) {
        Class clz = criteria.getClz();

        List<Object> valueList = criteria.getValueList();

        SqlParsed sqlParsed = this.criteriaParser.parse(criteria);

        String sql = sqlParsed.getSql().toString();

        int page = criteria.getPage();
        int rows = criteria.getRows();

        int start = (page - 1) * rows;

        sql = dialect.match(sql, start, rows);
        if (ConfigAdapter.isIsShowSql())
            System.out.println(sql);

        List<T> list = new ArrayList<>();

        PreparedStatement pstmt = null;
        BeanElement tempEle = null;
        try {
            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(sql);

            int i = 1;
            for (Object value : valueList) {
                value = this.dialect.filterValue(value);
                this.dialect.setObject(i++, value, pstmt);
            }

            ResultSet rs = pstmt.executeQuery();

            if (rs != null) {

                List<BeanElement> eles = MapperFactory.getElementList(clz);

                while (rs.next()) {

                    T obj = (T) clz.newInstance();
                    list.add(obj);
                    initObj(obj, rs, tempEle, eles);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            String str = tempEle==null?"":(tempEle.property+"|"+tempEle.getMapper());
            throw new RollbackException(
                    "Exception occured by class = " + clz.getName() + ",column："+ str + ", message: " + e.getMessage());
        } finally {
            close(pstmt);
            close(conn);
        }

        return list;
    }

    protected <T> Page<T> find(Criteria criteria, Connection conn) {
        Class clz = criteria.getClz();

        List<Object> valueList = criteria.getValueList();

        SqlParsed sqlParsed = this.criteriaParser.parse(criteria);

        String sql = sqlParsed.getSql().toString();

        int page = criteria.getPage();
        int rows = criteria.getRows();

        int start = (page - 1) * rows;

        sql = dialect.match(sql, start, rows);

        if (ConfigAdapter.isIsShowSql())
            System.out.println(sql);

        Page<T> pagination = new Page<T>();
        pagination.setClz(clz);
        pagination.setPage(page == 0 ? 1 : page);
        pagination.setRows(rows == 0 ? Integer.MAX_VALUE : rows);
        pagination.setSortList(criteria.getSortList());
        pagination.setScroll(criteria.isScroll());

        List<T> list = pagination.getList();

        PreparedStatement pstmt = null;
        BeanElement tempEle = null;
        try {
            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(sql);

            int i = 1;
            for (Object value : valueList) {
                value = this.dialect.filterValue(value);
                this.dialect.setObject(i++, value, pstmt);
            }

            ResultSet rs = pstmt.executeQuery();

            if (rs != null) {

                List<BeanElement> eles = MapperFactory.getElementList(clz);

                while (rs.next()) {
                    T obj = (T) clz.newInstance();
                    list.add(obj);
                    initObj(obj, rs, tempEle, eles);
                }

                Parsed parsed = Parser.get(clz);
                ResultSortUtil.sort(list,criteria,parsed);

                long count = 0;
                if (!criteria.isScroll()) {
                    int size = list.size();
                    if (page == 0) {
                        count = size;
                    } else if (size > 0) {
                        String sqlCount = sqlParsed.getCountSql();
                        count = getCount(sqlCount, valueList);
                    }
                    pagination.setTotalRows(count);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RollbackException(
                    "Exception occured by class = " + clz.getName() + ", message: " + e.getMessage());
        } finally {
            close(pstmt);
            close(conn);
        }

        return pagination;
    }

    @Override
    public <T> Page<T> find(Criteria criteria) {

        Connection conn = RcDataSourceUtil.getConnection();
        return find(criteria, conn);
    }

    @Override
    public <T> List<T> list(Criteria criteria) {

        Connection conn = RcDataSourceUtil.getConnection();
        return list(criteria, conn);
    }

    /**
     * getCount
     *
     * @param sql
     * @param set
     * @return
     */
    private long getCount(String sql, Collection<Object> set) {

        long count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = RcDataSourceUtil.getConnection();
            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(sql);

            int i = 1;
            for (Object value : set) {
                value = this.dialect.filterValue(value);
                this.dialect.setObject(i++, value, pstmt);
            }

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getLong("count");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(pstmt);
            close(conn);
        }

        return count;
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

        sql = sql.replace(" drop ", SqlScript.SPACE).replace(" delete ", SqlScript.SPACE).replace(" insert ", SqlScript.SPACE).replace(";", SqlScript.SPACE); // 手动拼接SQL,
        // 必须考虑应用代码的漏洞
        sql = SqlParserUtil.mapper(sql, parsed);

        if (ConfigAdapter.isIsShowSql())
            System.out.println(sql);

        boolean b = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {

            conn = DataSourceUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            b = pstmt.executeUpdate() == 0 ? false : true;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            close(pstmt);
            DataSourceUtil.releaseConnection(conn);
        }

        return b;
    }

    protected boolean refreshByCondition(RefreshCondition refreshCondition, Connection conn) {

        @SuppressWarnings("rawtypes")
        Class clz = refreshCondition.getClz();

        Parsed parsed = Parser.get(clz);

        String tableName = parsed.getTableName();
        StringBuilder sb = new StringBuilder();
        sb.append(SqlScript.UPDATE).append(SqlScript.SPACE).append(tableName).append(SqlScript.SPACE);
        String sql = SqlUtil.concatRefresh(sb, parsed, refreshCondition, this.criteriaParser);

        if (ConfigAdapter.isIsShowSql())
            System.out.println("________refreshByCondition: " + sql);

        if (sql.contains("SET  WHERE"))
            return false;

        boolean flag = false;

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);

            int i = 1;

            SqlUtil.adpterRefreshCondition(pstmt, i, refreshCondition.getCondition());

            flag = pstmt.executeUpdate() == 0 ? false : true;

        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
            throw new RollbackException(
                    "Exception occured by class = " + clz.getName() + ", message: " + ExceptionUtil.getMessage(e));

        } finally {
            close(pstmt);
            DataSourceUtil.releaseConnection(conn);
        }

        return flag;
    }


    @Override
    public <T> boolean refreshByCondition(RefreshCondition<T> refreshCondition) {

        Connection conn = null;
        try {
            conn = DataSourceUtil.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("NO CONNECTION");
        }

        return refreshByCondition(refreshCondition, conn);
    }

    @Override
    public <T> List<T> in(InCondition inCondition) {

        Class<T> clz = inCondition.getClz();
        String inProperty = inCondition.getProperty();
        List<? extends Object> inList = inCondition.getInList();

        Parsed parsed = Parser.get(clz);

        if (StringUtil.isNullOrEmpty(inProperty)) {
            inProperty = parsed.getKey(X.KEY_ONE);
            if (Objects.isNull(inProperty))
                throw new PersistenceException("No setting of PrimaryKey by @X.Key");
        }

        BeanElement be = parsed.getElement(inProperty);
        if (be == null)
            throw new RuntimeException(
                    "Exception in method: <T> List<T> in(inCondition), no property: "
                            + inProperty);


        String sql = MapperFactory.getSql(clz, Mapper.LOAD);
        List<BeanElement> eles = MapperFactory.getElementList(clz);

        String mapper = parsed.getMapper(inProperty);

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

        sql = sb.toString();
        if (ConfigAdapter.isIsShowSql())
            System.out.println(sql);

        List<T> list = new ArrayList<T>();// return list

        Connection conn = null;
        PreparedStatement pstmt = null;
        BeanElement tempEle = null;
        try {
            conn = RcDataSourceUtil.getConnection();
            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    T obj = clz.newInstance();
                    list.add(obj);
                    initObj(obj, rs, tempEle, eles);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RollbackException(
                    "Exception occured by class = " + clz.getName() + ", message: " + e.getMessage());
        } finally {
            close(pstmt);
            close(conn);
        }

        return list;
    }

    @Override
    public Page<Map<String, Object>> find(Criteria.ResultMappedCriteria resultMapped) {

        Connection conn = RcDataSourceUtil.getConnection();

        return this.find(resultMapped, conn);
    }

    protected Page<Map<String, Object>> find(Criteria.ResultMappedCriteria resultMapped, Connection conn) {

        List<Object> valueList = resultMapped.getValueList();

        SqlParsed  sqlParsed = this.criteriaParser.parse(resultMapped);

        String sqlCount = sqlParsed.getCountSql();
        String sql = sqlParsed.getSql().toString();

        int page = resultMapped.getPage();
        int rows = resultMapped.getRows();

        int start = (page - 1) * rows;

        sql = dialect.match(sql, start, rows);
        if (ConfigAdapter.isIsShowSql())
            System.out.println(sql);

        Page<Map<String, Object>> pagination = new Page<Map<String, Object>>();
        pagination.setClz(Map.class);
        pagination.setPage(page == 0 ? 1 : page);
        pagination.setRows(rows == 0 ? Integer.MAX_VALUE : rows);
        pagination.setSortList(resultMapped.getSortList());
        pagination.setScroll(resultMapped.isScroll());

        List<Map<String,Object>> list = pagination.getList();

        PreparedStatement pstmt = null;
        try {
            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(sql);

            int i = 1;
            for (Object value : valueList) {
                value = this.dialect.filterValue(value);
                this.dialect.setObject(i++, value, pstmt);
            }


            ResultSet rs = pstmt.executeQuery();

            if (rs != null) {

                List<String> resultKeyList = resultMapped.getResultKeyList();
                if (resultKeyList.isEmpty()) {
                    resultKeyList = resultMapped.listAllResultKey();
                }

                while (rs.next()) {
                    Map<String, Object> mapR = new HashMap<String, Object>();
                    list.add(mapR);

                    for (String property : resultKeyList) {
                        String mapper = resultMapped.getMapMapper().mapper(property);
                        Object obj = this.dialect.mappedResult(property, mapper,resultMapped.getAliaMap(), resultMapped.getResultAliaMap(),rs);
                        mapR.put(property, obj);
                    }
                }

                ResultSortUtil.sort(list,resultMapped);

                long count = 0;
                if (!resultMapped.isScroll()) {
                    int size = pagination.getList().size();
                    if (page == 0) {
                        count = size;
                    } else if (size > 0) {
                        count = getCount(sqlCount, valueList);
                    }
                    pagination.setTotalRows(count);
                }

                String resultKey0 = resultKeyList.get(0);
                if (!resultKey0.contains("."))
                    return pagination;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(pstmt);
            close(conn);
        }


        List<Map<String, Object>> stringKeyMapList = pagination.getList();
        if (!stringKeyMapList.isEmpty()) {
            List<Map<String, Object>> jsonableMapList = BeanMapUtil.toJsonableMapList(stringKeyMapList);
            pagination.reSetList(jsonableMapList);
        }

//        BeanUtilX.aliaToClzzForMapResult(resultMapped,pagination.getList());

        return pagination;
    }

    @Override
    public List<Map<String, Object>> list(Criteria.ResultMappedCriteria resultMapped) {

        List<Map<String, Object>> list = new ArrayList<>();

        List<Object> valueList = resultMapped.getValueList();

        SqlParsed sqlParsed = this.criteriaParser.parse(resultMapped);

        String sql = sqlParsed.getSql().toString();

        int page = resultMapped.getPage();
        int rows = resultMapped.getRows();
        int start = (page - 1) * rows;

        sql = dialect.match(sql, start, rows);
        if (ConfigAdapter.isIsShowSql())
            System.out.println(sql);

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = RcDataSourceUtil.getConnection();
            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(sql);

            int i = 1;
            for (Object value : valueList) {
                value = this.dialect.filterValue(value);
                this.dialect.setObject(i++, value, pstmt);
            }

            ResultSet rs = pstmt.executeQuery();

            if (rs != null) {

                List<String> resultKeyList = resultMapped.getResultKeyList();
                if (resultKeyList.isEmpty()) {
                    resultKeyList = resultMapped.listAllResultKey();// FIXME ALLWAYS BUG
                }

                while (rs.next()) {
                    Map<String, Object> mapR = new HashMap<String, Object>();
                    list.add(mapR);

                    for (String property : resultKeyList) {
                        String mapper = resultMapped.getMapMapper().mapper(property);
                        Object obj = this.dialect.mappedResult(property, mapper, resultMapped.getAliaMap(),resultMapped.getResultAliaMap(),rs);
                        mapR.put(property, obj);
                    }

                }

                String resultKey0 = resultKeyList.get(0);
                if (!resultKey0.contains("."))
                    return list;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(pstmt);
            close(conn);
        }

        if (!list.isEmpty()) {
            List<Map<String, Object>> mapList = BeanMapUtil.toJsonableMapList(list);
            return mapList;
        }

//        BeanUtilX.aliaToClzzForMapResult(resultMapped,list);

        return list;
    }

    @Override
    public <T> T getOne(T conditionObj) {
        Connection conn = RcDataSourceUtil.getConnection();
        return getOne(conditionObj ,conn);
    }

    private <T> void initObj(T obj, ResultSet rs, BeanElement tempEle, List<BeanElement> eles)
            throws Exception {

        ResultSetUtil.initObj(obj, rs, tempEle, eles);
    }

    protected <T> T getOne(T conditionObj, Connection conn) {

        List<T> list = list(conditionObj, conn);

        if (list.isEmpty())
            return null;
        return list.get(0);
    }

}
