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
package io.xream.x7;

import io.xream.sqli.api.Dialect;
import io.xream.sqli.api.JdbcWrapper;
import io.xream.sqli.api.TemporaryRepository;
import io.xream.sqli.cache.L2CacheResolver;
import io.xream.sqli.repository.api.CriteriaToSql;
import io.xream.sqli.repository.api.Repository;
import io.xream.sqli.repository.cache.CacheableRepository;
import io.xream.sqli.repository.dao.*;
import io.xream.sqli.repository.internal.DefaultTemporaryRepository;
import io.xream.sqli.repository.mapper.DefaultTemporaryTableParser;
import io.xream.sqli.repository.mapper.MapperFactory;
import io.xream.sqli.repository.transform.DataTransform;
import io.xream.sqli.repository.transform.SqlDataTransform;
import io.xream.sqli.starter.DbType;
import io.xream.sqli.starter.ManuRepositoryStarter;
import io.xream.x7.cache.DefaultL2CacheResolver;
import io.xream.x7.repository.id.DefaultIdGeneratorService;
import io.xream.x7.repository.id.IdGeneratorService;
import io.xream.x7.repository.jdbctemplate.JdbcTemplateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;


public class RepositoryStarter  {

    private Logger logger = LoggerFactory.getLogger(RepositoryStarter.class);

    @Bean
    @Order(2)
    public Dialect dialect(Environment environment){

        String driverClassName = getDbDriverKey(environment);

        Dialect dialect = null;
        try {
            if (driverClassName.contains(DbType.MYSQL)) {
                DbType.value = DbType.MYSQL;
                dialect = (Dialect) Class.forName("io.xream.sqli.dialect.MySqlDialect").newInstance();
            } else if (driverClassName.contains(DbType.ORACLE)) {
                DbType.value = DbType.ORACLE;
                dialect = (Dialect) Class.forName("io.xream.sqli.dialect.OracleDialect").newInstance();
            }
            initDialect(dialect);
        }catch (Exception e){
            e.printStackTrace();
        }

        return dialect;
    }

    @Bean
    @Order(3)
    public CriteriaToSql criteriaParser(Dialect dialect) {

        CriteriaToSql criteriaParser =  new DefaultCriteriaToSql();
        criteriaParser.setDialect(dialect);

        return criteriaParser;
    }

    @Bean
    @Order(4)
    public TemporaryRepository.Parser temporaryTableParser(Dialect dialect){
        DefaultTemporaryTableParser temporaryTableParser = new DefaultTemporaryTableParser();
        temporaryTableParser.setDialect(dialect);
        return temporaryTableParser;
    }

    @Bean
    @Order(5)
    public Dao dao(Environment environment){

        String driverClassName = getDbDriverKey(environment);

        Dao dao =  null;
        if (driverClassName.toLowerCase().contains("mysql")
                || driverClassName.toLowerCase().contains("oracle")) {
            dao = new DaoImpl();
        }
        return dao;
    }

    @Bean
    @Order(6)
    public L2CacheResolver cacheResolver(){
        return new DefaultL2CacheResolver();
    }

    @Bean
    @Order(7)
    public IdGeneratorService idGenerator(){
        DefaultIdGeneratorService idGenerator = new DefaultIdGeneratorService();
        return idGenerator;
    }

    @Bean
    @Order(8)
    public Repository dataRepository(Dao dao, L2CacheResolver cacheResolver, Environment environment){

        String driverClassName = getDbDriverKey(environment);

        DataTransform dataTransform = null;
        if (driverClassName.toLowerCase().contains("mysql")
                || driverClassName.toLowerCase().contains("oracle")) {
            dataTransform = new SqlDataTransform();
            ((SqlDataTransform) dataTransform).setDao(dao);
        }

        CacheableRepository repository = new CacheableRepository();
        repository.setDataTransform(dataTransform);
        repository.setCacheResolver(cacheResolver);

        ManuRepositoryStarter.init(repository);

        return repository;
    }


    @ConditionalOnMissingBean(X7Data.class)
    @Bean
    @Order(9)
    public X7Data enableData(){
        return new X7Data();
    }


    @Bean
    @Order(10)
    public TemporaryDao temporaryDao(){
        return new TemporaryDaoImpl();
    }

    @Bean
    @Order(11)
    public TemporaryRepository temporaryRepository(Dao dao,TemporaryDao temporaryDao,TemporaryRepository.Parser temporaryTableParser,Environment environment){
        String driverClassName = getDbDriverKey(environment);
        DefaultTemporaryRepository temporaryRepository = new DefaultTemporaryRepository();
        DataTransform dataTransform = null;
        if (driverClassName.toLowerCase().contains("mysql")
                || driverClassName.toLowerCase().contains("oracle")) {
            dataTransform = new SqlDataTransform();
            ((SqlDataTransform) dataTransform).setDao(dao);
        }
        temporaryRepository.setDataTransform(dataTransform);
        temporaryRepository.setTemporaryDao(temporaryDao);
        temporaryRepository.setTemporaryRepositoryParser(temporaryTableParser);

        return temporaryRepository;
    }

    @Bean
    @Order(12)
    public JdbcWrapper jdbcWrapper(){
        return new JdbcTemplateWrapper();
    }

    /**
     * TODO:
     *      改成Map,可以动态获取方言
     * @param dialect
     */
    private void initDialect(Dialect dialect) {
        MapperFactory.Dialect = dialect;
    }

    private String getDbDriverKey(Environment environment) {
        String driverClassName = null;
        try {
            driverClassName = environment.getProperty("spring.datasource.driver-class-name");
        }catch (Exception e){

        }
        if (driverClassName == null)
            return "mysql";
        return driverClassName.toLowerCase();
    }

}
