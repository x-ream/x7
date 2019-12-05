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


import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceHolder {

    public final static String WRITEABLE_DEFAULT = "dataSource";
    public final static String READABLE_DEFAULT = "readableDataSource";

    private static DataSource dataSource;
    private static DataSource readableDataSource;

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void setDataSource(DataSource dataSource) {
        DataSourceHolder.dataSource = dataSource;
        if (dataSource != null)
            put(WRITEABLE_DEFAULT,dataSource);
    }

    public static DataSource getReadableDataSource() {
        return readableDataSource;
    }

    public static void setReadableDataSource(DataSource readableDatasource) {
        DataSourceHolder.readableDataSource = readableDatasource;
        if (readableDatasource != null)
            put(READABLE_DEFAULT,readableDatasource);
    }

    private final static Map<String,DataSource> map = new ConcurrentHashMap<>();

    public static void put(String key, DataSource dataSource){
        map.put(key, dataSource);
    }

    public static DataSource get(String key) {
        return map.get(key);
    }

}
