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
package x7;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import x7.core.config.ConfigAdapter;
import x7.core.repository.CacheResolver;
import x7.core.util.StringUtil;
import x7.distributed.LockStorage;
import x7.repository.*;
import x7.repository.cache.DefaultL2CacheResolver;
import x7.repository.cache.DefaultL2CacheStoragePolicy;
import x7.repository.dao.Dao;
import x7.repository.dao.DaoImpl;
import x7.repository.dao.SqlCriteriaParser;
import x7.repository.dao.TxConfig;
import x7.repository.id.DefaultIdGenerator;
import x7.repository.id.DefaultIdGeneratorPolicy;
import x7.repository.id.IdGeneratorPolicy;
import x7.repository.internal.DomainObjectRepositoy;
import x7.repository.mapper.Mapper;
import x7.repository.mapper.MapperFactory;
import x7.repository.transform.DataTransform;
import x7.repository.transform.SqlDataTransform;
import x7.repository.util.ResultSetUtil;

import javax.sql.DataSource;
import java.util.Objects;

@EnableConfigurationProperties({
        DataSourceProperties_R.class})
public class RepositoryStarter  {

    private Logger logger = LoggerFactory.getLogger(RepositoryStarter.class);


    @Bean
    @Order(2)
    public Mapper.Dialect dialect(Environment environment){
        String driverClassName = environment.getProperty("spring.datasource.driver-class-name");

        String driver = null;
        if (Objects.isNull(driverClassName)) {
            driver = environment.getProperty("x7.db.driver");
        } else {
            driver = driverClassName;
        }

        driver = driver.toLowerCase();
        Mapper.Dialect dialect = null;
        try {
            if (driver.contains(DbType.MYSQL)) {
                DbType.value = DbType.MYSQL;
                dialect = (Mapper.Dialect) Class.forName("x7.repository.dialect.MySqlDialect").newInstance();
            } else if (driver.contains(DbType.ORACLE)) {
                DbType.value = DbType.ORACLE;
                dialect = (Mapper.Dialect) Class.forName("x7.repository.dialect.OracleDialect").newInstance();
            }
            initDialect(dialect);
        }catch (Exception e){

        }

        return dialect;
    }

    @Bean
    @Order(3)
    public CriteriaParser criteriaParser(Mapper.Dialect dialect,Environment environment) {

        String driverClassName = environment.getProperty("spring.datasource.driver-class-name");

        CriteriaParser criteriaParser =  null;

        if (driverClassName.toLowerCase().contains("mysql")
                || driverClassName.toLowerCase().contains("oracle")) {
            criteriaParser = new SqlCriteriaParser();
            criteriaParser.setDialect(dialect);
        }

        return criteriaParser;
    }


    @Bean
    @Order(4)
    public Dao dao(Mapper.Dialect dialect,CriteriaParser criteriaParser,Environment environment){

        String driverClassName = environment.getProperty("spring.datasource.driver-class-name");

        Dao dao =  null;

        if (driverClassName.toLowerCase().contains("mysql")
                || driverClassName.toLowerCase().contains("oracle")) {
            dao = new DaoImpl();
            ((DaoImpl) dao).setDialect(dialect);
            ((DaoImpl) dao).setCriteriaParser(criteriaParser);
        }

        return dao;
    }

    @Bean
    @Order(5)
    public CacheResolver cacheResolver(StringRedisTemplate stringRedisTemplate){

        DefaultL2CacheStoragePolicy cacheStoragePolicy = new DefaultL2CacheStoragePolicy();
        cacheStoragePolicy.setStringRedisTemplate(stringRedisTemplate);
        DefaultL2CacheResolver levelTwoCacheResolver = new DefaultL2CacheResolver();
        levelTwoCacheResolver.setCacheStoragePolicy(cacheStoragePolicy);

        return levelTwoCacheResolver;
    }

    @Bean
    @Order(6)
    public IdGeneratorPolicy idGeneratorPolicy(StringRedisTemplate stringRedisTemplate){
        DefaultIdGeneratorPolicy defaultIdGeneratorPolicy =  new DefaultIdGeneratorPolicy();
        defaultIdGeneratorPolicy.setStringRedisTemplate(stringRedisTemplate);
        return defaultIdGeneratorPolicy;
    }

    @Bean
    @Order(7)
    public Repository.IdGenerator idGenerator(IdGeneratorPolicy policy){
        DefaultIdGenerator idGenerator = new DefaultIdGenerator();
        idGenerator.setIdGeneratorPolicy(policy);
        return idGenerator;
    }

    @Bean
    @Order(8)
    public Repository dataRepository(Dao dao, CacheResolver cacheResolver,Environment environment){

        String driverClassName = environment.getProperty("spring.datasource.driver-class-name");

        DataTransform dataTransform = null;

        if (driverClassName.toLowerCase().contains("mysql")
                || driverClassName.toLowerCase().contains("oracle")) {
            dataTransform = new SqlDataTransform();
            ((SqlDataTransform) dataTransform).setDao(dao);
        }

        CacheableRepository repository = new CacheableRepository();
        repository.setDataTransform(dataTransform);
        repository.setCacheResolver(cacheResolver);

        return repository;
    }


    @Bean
    @Order(9)
    public DomainObjectRepositoy domainObjectRepositoy(Repository repository) {
        DomainObjectRepositoy domainObjectRepositoy = new DomainObjectRepositoy();
        domainObjectRepositoy.setRepository(repository);
        return domainObjectRepositoy;
    }

    @ConditionalOnMissingBean(X7Data.class)
    @Bean
    @Order(10)
    public X7Data enableData(DataSource dataSource, DataSourceProperties dataSourceProperties, DataSourceProperties_R dataSourceProperties_r,Environment env,
                             StringRedisTemplate stringRedisTemplate){

        DataSource writeDataSource = dataSource;

        /*
         * Spring Boot多数据源不友好,另外读库可以不需要事务<br>
         * 1. 对于Sharding, 可以用动态数据源<br>
         * 2. 只读库，一个请求只需并成一个连接，可绕开事务<br>
         */
        DataSource readDataSource = getReadDataSource(dataSourceProperties,dataSourceProperties_r);

        configX7Datasource(writeDataSource, readDataSource,env);

        {//初始化分布式锁
            LockStorage lockStorage = new LockStorage();
            lockStorage.setStringRedisTemplate(stringRedisTemplate);
        }


        return new X7Data();
    }

    @ConditionalOnMissingBean(TxConfig.class)
    @Bean
    @Order(11)
    public TxConfig txConfig(DataSourceTransactionManager dstm){
        return new TxConfig(dstm);
    }


    public HikariDataSource getReadDataSource(DataSourceProperties dataSourceProperties,DataSourceProperties_R dataSourceProperties_r) {


        if (Objects.isNull(dataSourceProperties_r.getUrl())) {
            logger.info("Readable DataSource Config Key: spring.datasource.read.value");
            logger.info("Readable DataSource Config Value: null");
            logger.info("Readable DataSource Ignored");

            return null;
        }

        String driverClassName = dataSourceProperties.determineDriverClassName();
        String username = dataSourceProperties.getUsername();
        String password = dataSourceProperties.getPassword();

        if (Objects.nonNull(dataSourceProperties_r.getDriverClassName())) {
            driverClassName = dataSourceProperties_r.getDriverClassName();
        }

        if (Objects.nonNull(dataSourceProperties_r.getUsername())) {
            username = dataSourceProperties_r.getUsername();
        }

        if (Objects.nonNull(dataSourceProperties_r.getPassword())) {
            password = dataSourceProperties_r.getPassword();
        }

        HikariDataSource dsR = new HikariDataSource();
        dsR.setJdbcUrl(dataSourceProperties_r.getUrl());
        dsR.setUsername(username);
        dsR.setPassword(password);
        dsR.setDriverClassName(driverClassName);
        dsR.setMaximumPoolSize(dataSourceProperties_r.getMaximumPoolSize());

        logger.info("Readable DataSource Created, value:"+dataSourceProperties_r.getUrl());
        return dsR;
    }


    public void configX7Datasource(DataSource dsW, DataSource dsR,Environment env) {//FIXME

        if (Objects.isNull(dsW))
            throw new RuntimeException("Writeable DataSource Got NULL");

        String showSqlStr = env.getProperty("x7.repository.show-sql");
        boolean showSql = false;
        if (StringUtil.isNotNull(showSqlStr)){
            try {
                showSql = Boolean.parseBoolean(showSqlStr);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (showSql) {
            ConfigAdapter.setIsShowSql(true);
        }else{
            logger.info("X7 Repsository will not show SQL, for no config like one of: x7.repository.show-sql=true" );
        }

        DataSourceSetter.set(dsW, dsR);
    }

    /**
     * TODO:
     *      改成Map,可以动态获取方言
     * @param dialect
     */
    private void initDialect(Mapper.Dialect dialect) {
        MapperFactory.Dialect = dialect;
        ResultSetUtil.dialect = dialect;
    }

}
