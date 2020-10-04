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

import io.xream.sqli.dialect.Dialect;
import io.xream.sqli.dialect.DynamicDialect;
import io.xream.sqli.repository.init.SqlInitFactory;
import io.xream.x7.base.util.StringUtil;
import org.springframework.core.env.Environment;

/**
 * @Author Sim
 */
public interface DialectAdapter {
    String MYSQL = "mysql";
    String CLICKHOUSE = "clickhouse";
    String OCEANBASE = "oceanbase";
    String ORACLE = "oracle";

    default Dialect adapter(String driverClassName) {

        isSupported(driverClassName);

        try {
            Dialect dialect = null;
            if (driverClassName.contains(MYSQL) || driverClassName.contains(OCEANBASE)) {
                dialect = (Dialect) Class.forName("io.xream.sqli.dialect.MySqlDialect").newInstance();
            }else if (driverClassName.contains(CLICKHOUSE)) {
                dialect = (Dialect) Class.forName("io.xream.sqli.dialect.ClickhouseDialect").newInstance();
            }else if (driverClassName.contains(ORACLE)) {
                dialect = (Dialect) Class.forName("io.xream.sqli.dialect.OracleDialect").newInstance();
            }

            SqlInitFactory.DIALECT = dialect;

            DynamicDialect dynamicDialect = new DynamicDialect();
            dynamicDialect.setDefaultDialect(dialect);

            return dynamicDialect;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

     static boolean isSupported(String driverClassName) {
        return driverClassName.toLowerCase().contains(MYSQL)
                ||driverClassName.toLowerCase().contains(CLICKHOUSE)
                ||driverClassName.toLowerCase().contains(OCEANBASE)
                || driverClassName.toLowerCase().contains(ORACLE);
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
