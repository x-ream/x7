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

import x7.core.config.ConfigAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * X7-repository defect design readable datasource <br>
 * only read committed <br>
 * X7-repository doesn't suggest for system dependent on database tx <br>
 */
public class RcDataSourceUtil {

    /**
     * <Key, Connection>
     */
    private final static Map<String, Connection> connectionMap = new ConcurrentHashMap<String, Connection>();

    /**
     * <ThreadId, Key>
     */
    private final static Map<String, String> keyMap = new ConcurrentHashMap<>();



    public static Connection getConnection() {

        try{
            return getConnection0();
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static Connection getConnection0() throws Exception {

        String key = getKey();

        DataSource ds = getDataSourceReadable();

        Connection conn = null;

        if (key == null) {

            conn = ds.getConnection();
            if (conn == null) {
                try {
                    TimeUnit.MICROSECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                conn = ds.getConnection();
            }

        } else {
            conn = connectionMap.get(key);
            if (conn == null) {

                conn = ds.getConnection();
                if (conn == null) {
                    try {
                        TimeUnit.MICROSECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    conn = ds.getConnection();
                }

                connectionMap.put(key, conn);

            }
        }

        if (conn == null) {
            System.out.println("_______NO CONNECTION");
            throw new RuntimeException("NO CONNECTION");
        }

        return conn;
    }


    protected static void releaseConnection(Connection conn){

        String key = getKey();

        if (key == null) {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("RcDataSourceUtil.releaseConnection Exception: " + e.getMessage());
            }
            return;
        }

    }

    public static void end(){

        String key = getKey();

        if (key == null)
            return;

        Connection conn = connectionMap.remove(key);

        if (conn != null){
            try {
                conn.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        String threadId = String.valueOf(Thread.currentThread().getId());
        keyMap.remove(threadId);
    }

    private static String getKey() {
        String threadId = String.valueOf(Thread.currentThread().getId());
        String key = keyMap.get(threadId);
        return key;
    }

    private static DataSource getDataSourceReadable(){
        DataSource ds = DataSourceHolder.getReadableDataSource();
        if (ds == null) {
            ds = DataSourceHolder.getDataSource();
        }else{
            if (ConfigAdapter.isIsShowSql())
                System.out.println("Find By Read DataSource: " + ds );
        }
        return ds;
    }

    public static void  start(){
        String threadId = String.valueOf(Thread.currentThread().getId());
        String key = "rcTx."+threadId;
        keyMap.put(threadId,key);
    }


}
