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
package io.xream.x7.sqli.repository.dialect;

import io.xream.internal.util.StringUtil;
import io.xream.sqli.dialect.Dialect;
import org.springframework.core.env.Environment;

/**
 * @author Sim
 */
public interface DialectAdapter {
    String MYSQL = "mysql"; //mysql, oceanbase, tidb, presto ....
    String IMPALA = "impala"; //upsert ,while mysql replace
    String CLICKHOUSE = "clickhouse";
    String POSTGRESQL = "postgresql";
    String ORACLE = "oracle";
    String TAOS = "taosdata";
    //not support: db2, sqlserver, sybase,

    default Dialect adapter(String driverClassName) {

        try {
            Dialect dialect = null;
            if (driverClassName.contains(POSTGRESQL)) {
                dialect = (Dialect) Class.forName("io.xream.sqli.dialect.MySqlDialect").newInstance();//TODO , buildTableSql, upsert
            }else if (driverClassName.contains(IMPALA)) {
                dialect = (Dialect) Class.forName("io.xream.sqli.dialect.ImpalaDialect").newInstance();
            }else if (driverClassName.contains(CLICKHOUSE)) {
                dialect = (Dialect) Class.forName("io.xream.sqli.dialect.ClickhouseDialect").newInstance();
            }else if (driverClassName.contains(ORACLE)) {
                dialect = (Dialect) Class.forName("io.xream.sqli.dialect.OracleDialect").newInstance();
            }else if (driverClassName.contains(TAOS)){
                dialect =(Dialect) Class.forName("io.xream.sqli.dialect.TAOSDialect").newInstance();
            }else {
                dialect = (Dialect) Class.forName("io.xream.sqli.dialect.MySqlDialect").newInstance();
            }

            return dialect;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    default String getDbDriverKey(Environment environment) {
        String driverClassName = null;
        try {
            driverClassName = environment.getProperty("spring.datasource.driver-class-name");
            if (StringUtil.isNullOrEmpty(driverClassName)) {
                driverClassName = environment.getProperty("spring.datasource.driverClassName");
            }
        }catch (Exception e) {
        }

        if (driverClassName == null)
            return MYSQL;
        return driverClassName.toLowerCase();
    }
}
