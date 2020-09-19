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

import io.xream.sqli.api.NativeRepository;
import io.xream.sqli.api.TemporaryRepository;
import io.xream.sqli.builder.CriteriaToSql;
import io.xream.sqli.cache.L2CacheResolver;
import io.xream.sqli.core.Dialect;
import io.xream.sqli.core.JdbcWrapper;
import io.xream.sqli.core.Repository;
import io.xream.sqli.repository.init.SqlInitFactory;
import io.xream.sqli.starter.DbType;
import io.xream.sqli.starter.SqliStarter;
import io.xream.x7.cache.DefaultL2CacheResolver;
import io.xream.x7.repository.id.DefaultIdGeneratorService;
import io.xream.x7.repository.id.IdGeneratorService;
import io.xream.x7.repository.jdbctemplate.JdbcTemplateWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;


public class RepositoryStarter  {

    @Bean
    @Order(1)
    public CriteriaToSql criteriaToSql() {
        return  SqliStarter.getInstance().criteriaToSql();
    }

    @Bean
    @Order(2)
    public Dialect dialect(Environment environment){

        String driverClassName = getDbDriverKey(environment);

        isSupported(driverClassName);

        Dialect dialect = null;
        try {
            if (driverClassName.contains(DbType.MYSQL)
                ||driverClassName.contains(DbType.CLICKHOUSE)
            ) {
                DbType.setValue(DbType.MYSQL);
                dialect = (Dialect) Class.forName("io.xream.sqli.dialect.MySqlDialect").newInstance();
            } else if (driverClassName.contains(DbType.ORACLE)) {
                DbType.setValue(DbType.ORACLE);
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
    public L2CacheResolver cacheResolver(){
        return new DefaultL2CacheResolver();
    }

    @Bean
    @Order(4)
    public IdGeneratorService idGenerator(){
        return new DefaultIdGeneratorService();
    }

    @Bean
    @Order(5)
    public JdbcWrapper jdbcWrapper(){
        return new JdbcTemplateWrapper();
    }


    @Bean
    @Order(6)
    public Repository dataRepository(CriteriaToSql criteriaToSql, JdbcWrapper jdbcWrapper, Dialect dialect, L2CacheResolver cacheResolver, Environment environment){
        return SqliStarter.getInstance().repository(criteriaToSql,jdbcWrapper,dialect,cacheResolver);
    }

    @Bean
    @Order(7)
    public TemporaryRepository temporaryRepository(CriteriaToSql criteriaToSql, JdbcWrapper jdbcWrapper,Dialect dialect, Repository repository){
        return SqliStarter.getInstance().temporaryRepository(criteriaToSql,jdbcWrapper,dialect,repository);
    }

    @Bean
    @Order(8)
    public NativeRepository nativeRepository(Repository dataRepository){
        return SqliStarter.getInstance().nativeRepository(dataRepository);
    }


    /**
     * TODO:
     *      改成Map,可以动态获取方言
     * @param dialect
     */
    private void initDialect(Dialect dialect) {
        SqlInitFactory.Dialect = dialect;
    }

    private String getDbDriverKey(Environment environment) {
        String driverClassName = null;
        try {
            driverClassName = environment.getProperty("spring.datasource.driver-class-name");
        }catch (Exception e){

        }
        if (driverClassName == null)
            return DbType.MYSQL;
        return driverClassName.toLowerCase();
    }

    private boolean isSupported(String driverClassName){
        return driverClassName.toLowerCase().contains(DbType.MYSQL)
                ||driverClassName.toLowerCase().contains(DbType.CLICKHOUSE)
                || driverClassName.toLowerCase().contains(DbType.ORACLE);
    }

}
