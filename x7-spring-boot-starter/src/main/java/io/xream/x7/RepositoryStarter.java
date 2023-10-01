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
import io.xream.sqli.builder.internal.Q2Sql;
import io.xream.sqli.cache.internal.DefaultL2CacheResolver;
import io.xream.sqli.core.Repository;
import io.xream.sqli.dialect.Dialect;
import io.xream.sqli.repository.init.SqlInit;
import io.xream.sqli.spi.JdbcHelper;
import io.xream.sqli.spi.L2CacheResolver;
import io.xream.sqli.starter.SqliStarter;
import io.xream.x7.repository.jdbctemplate.JdbcTemplateHelper;
import io.xream.x7.sqli.repository.dialect.DialectAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;


public class RepositoryStarter  implements DialectAdapter {

    @Bean
    @Order(1)
    public Q2Sql __q2Sql() {
        return  SqliStarter.getInstance().q2Sql();
    }

    @Bean
    @Order(2)
    public Dialect __dialect(Environment environment){

        String driverClassName = getDbDriverKey(environment);

        Dialect dialect = adapter(driverClassName);

        return SqliStarter.getInstance().dialect(dialect);
    }


    @Bean
    @Order(3)
    public L2CacheResolver __cacheResolver(){
        return DefaultL2CacheResolver.newInstance();
    }


    @Bean
    @Order(4)
    public JdbcHelper __jdbcHelper(){
        return new JdbcTemplateHelper();
    }


    @Bean
    @Order(5)
    public Repository __repository(Q2Sql q2Sql, JdbcHelper jdbcHelper, Dialect dialect, L2CacheResolver cacheResolver){
        return SqliStarter.getInstance().repository(q2Sql, jdbcHelper,dialect,cacheResolver);
    }

    @Bean
    @Order(6)
    public TemporaryRepository __temporaryRepository(Q2Sql q2Sql, JdbcHelper jdbcHelper, Dialect dialect, Repository repository){
        return SqliStarter.getInstance().temporaryRepository(q2Sql, jdbcHelper,dialect,repository);
    }

    @Bean
    @Order(7)
    public NativeRepository __nativeRepository(Repository repository){
        return SqliStarter.getInstance().nativeRepository(repository);
    }

    @Bean
    @Order(8)
    public SqlInit __sqlInit(Dialect dialect){
        return SqliStarter.getInstance().sqlInit(dialect);
    }

}
