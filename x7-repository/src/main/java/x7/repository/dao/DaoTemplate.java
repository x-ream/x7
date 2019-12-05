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
import x7.core.bean.BeanElement;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.config.ConfigAdapter;
import x7.core.repository.X;
import x7.core.util.ExceptionUtil;
import x7.repository.CriteriaParser;
import x7.repository.KeyOne;
import x7.repository.exception.PersistenceException;
import x7.repository.exception.RollbackException;
import x7.repository.mapper.Mapper;
import x7.repository.mapper.MapperFactory;
import x7.repository.util.ResultSetUtil;
import x7.repository.util.SqlParserUtil;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Sim
 */
public class DaoTemplate {


    private final static Logger logger = LoggerFactory.getLogger(Dao.class);

    @Autowired
    private DaoTemplate template;

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


    public long execute(String sql,  StatementBuildable statementBuilder, IdBuildable idBuilder) {

        Connection conn = null;
        try {
            conn = DataSourceUtil.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("NO CONNECTION");
        }

        long id = 0;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);

            statementBuilder.buildStatement( pstmt);

            pstmt.execute();

            id = idBuilder.buildId(pstmt);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RollbackException("RollbackException: " + e.getMessage());
        } finally {
            close(pstmt);
            DataSourceUtil.releaseConnection(conn);
        }


        return id;
    }



    public boolean executeUpdate(String sql, StatementBuildable statementBuilder) {

        Connection conn = null;
        try {
            conn = DataSourceUtil.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("NO CONNECTION");
        }

        boolean flag;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);

            statementBuilder.buildStatement(pstmt);

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


    public ResultSet executeQuery(String sql,  StatementBuildable statementBuilder) {

        Connection conn = null;
        try {
            conn = DataSourceUtil.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("NO CONNECTION");
        }

        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            conn.setAutoCommit(true);
            pstmt = conn.prepareStatement(sql);

            statementBuilder.buildStatement( pstmt);

            rs =pstmt.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RollbackException("RollbackException: " + e.getMessage());
        } finally {
            close(pstmt);
            DataSourceUtil.releaseConnection(conn);
        }

        return rs;
    }


    public <T> List<T> list(Object conditionObj) {

        Class clz = conditionObj.getClass();

        String sql = MapperFactory.getSql(clz, Mapper.LOAD);

        Parsed parsed = Parser.get(clz);

        Map<String, Object> queryMap = SqlParserUtil.getQueryMap(parsed, conditionObj);
        sql = SqlUtil.concat(parsed, sql, queryMap);

        if (ConfigAdapter.isIsShowSql())
            System.out.println(sql);

        ResultSet rs = executeQuery(sql, new StatementBuildable() {
            @Override
            public void buildStatement(PreparedStatement pstmt) {
                int i = 1;
                try {
                    for (Object value : queryMap.values()) {
                        value = dialect.filterValue(value);
                        dialect.setObject(i++, value, pstmt);
                    }
                }catch (Exception e) {
                    throw new RuntimeException(ExceptionUtil.getMessage(e));
                }
            }


        });

        if (rs == null)
            return null;
        

        return null;
    }

    private <T> void initObj(T obj, ResultSet rs, BeanElement tempEle, List<BeanElement> eles)
            throws Exception {

        ResultSetUtil.initObj(obj, rs, tempEle, eles);
    }


    private <T> boolean testRemove(KeyOne<T> keyOne) {

        Class clz = keyOne.getClzz();

        Parsed parsed = Parser.get(clz);

        int i = 1;

        Field keyOneField = parsed.getKeyField(X.KEY_ONE);
        if (Objects.isNull(keyOneField))
            throw new PersistenceException("No setting of PrimaryKey by @X.Key");

        String sql = MapperFactory.getSql(clz, Mapper.REMOVE);

        return executeUpdate(sql,
                new StatementBuildable() {
                    @Override
                    public void buildStatement(PreparedStatement pstmt) {
                        SqlUtil.adpterSqlKey(pstmt, keyOneField, keyOne.get(), i);
                    }
                }
        );

    }


}