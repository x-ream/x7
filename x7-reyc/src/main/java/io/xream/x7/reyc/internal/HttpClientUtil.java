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
package io.xream.x7.reyc.internal;

import io.xream.x7.common.bean.KV;
import io.xream.x7.common.util.ExceptionUtil;
import io.xream.x7.common.util.JsonX;
import io.xream.x7.common.util.LoggerProxy;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;


public class HttpClientUtil {


    protected static String post(Class clz, String url, Object param, List<KV> hearderList, int connectTimeoutMS, int socketTimeoutMS, CloseableHttpClient httpclient) {

        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeoutMS)
                .setConnectTimeout(connectTimeoutMS)
                .setConnectionRequestTimeout(1000)
                .build();//设置请求和传输超时时间

        httpPost.setConfig(requestConfig);

        if (hearderList != null) {
            for (KV kv : hearderList) {
                httpPost.addHeader(kv.getK(), kv.getV().toString());
            }
        }

        String json = "";
        if (param != null) {
            json = JsonX.toJson(param);
        }
        HttpEntity entity = null;
        String result = null;
        try {
            entity = new ByteArrayEntity(json.getBytes("UTF-8"));
            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
            httpPost.setEntity(entity);
            LoggerProxy.info(clz,"Request: " + httpPost.getURI() + " -H Content-type:application/json -d " +json);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                    LoggerProxy.info(clz,"Response: " + result);

                }
            } finally {
                response.close();
            }
        } catch (HttpHostConnectException hce) {
            hce.printStackTrace();
            String str = "org.apache.http.conn.HttpHostConnectException: Connect to " + url + " failed: Connection refused: connect";
            throw new RuntimeException(str);
        } catch (ConnectTimeoutException cte){
            cte.printStackTrace();
            String str = "org.apache.http.conn.ConnectTimeoutException: Connect to " + url + " failed: Connection timeout: connect";
            throw new RuntimeException(str);
        } catch (UnknownHostException uhe){
            throw new RuntimeException(UnknownHostException.class.getName() + ": "+ExceptionUtil.getMessage(uhe));
        }catch(IOException ioe){
            throw new RuntimeException(IOException.class.getName() + ": "+ExceptionUtil.getMessage(ioe));
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }




    protected static String get(Class clz, String url,  List<KV> hearderList, int connectTimeoutMS, int socketTimeoutMS, CloseableHttpClient httpclient) {

        HttpGet httpGet = new HttpGet(url);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeoutMS)
                .setConnectTimeout(connectTimeoutMS)
                .setConnectionRequestTimeout(1000)
                .build();//设置请求和传输超时时间

        httpGet.setConfig(requestConfig);

        if (hearderList != null) {
            for (KV kv : hearderList) {
                httpGet.addHeader(kv.getK(), kv.getV().toString());
            }
        }


        HttpEntity entity = null;
        String result = null;
        try {
            httpGet.setHeader("Content-type", "application/json;charset=UTF-8");

            LoggerProxy.info(clz,"executing request " + httpGet.getURI());
            CloseableHttpResponse response = httpclient.execute(httpGet);
            try {
                entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                    LoggerProxy.info(clz,"Response content: " + result);

                }
            } finally {
                response.close();
            }
        } catch (HttpHostConnectException hce) {
            hce.printStackTrace();
            String str = "org.apache.http.conn.HttpHostConnectException: Connect to " + url + " failed: Connection refused: connect";
            throw new RuntimeException(str);
        } catch (ConnectTimeoutException cte){
            cte.printStackTrace();
            String str = "org.apache.http.conn.ConnectTimeoutException: Connect to " + url + " failed: Connection timeout: connect";
            throw new RuntimeException(str);
        } catch(IOException ioe){
            ioe.printStackTrace();
            throw new RuntimeException(ExceptionUtil.getMessage(ioe));
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


}
