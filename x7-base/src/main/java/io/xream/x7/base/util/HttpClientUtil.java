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
package io.xream.x7.base.util;

import io.xream.internal.util.ExceptionUtil;
import io.xream.internal.util.JsonX;
import io.xream.internal.util.StringUtil;
import io.xream.x7.base.KV;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class HttpClientUtil {

    private HttpClientUtil(){}

    private final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    public static String post(String url, Object param) {
        return post(url, param, null, 15000, 15000);
    }

    public static String post(String url, Object param, List<KV> hearderList, int connectTimeoutMS, int socketTimeoutMS, CloseableHttpClient httpclient) {

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
            logger.info("executing request " + httpPost.getURI());
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                    logger.info("Response content: " + result);

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

    public static String post(String url, Object param, List<KV> hearderList, int connectTimeoutMS, int socketTimeoutMS) {

        CloseableHttpClient httpclient = HttpClients.createDefault();

        return post(url,param,hearderList,connectTimeoutMS,socketTimeoutMS,httpclient);
    }

    public static String get(String urlString) {
        return get(urlString, null,15000, 15000);
    }

    public static String get(String url, List<KV> hearderList, int connectTimeoutMS, int socketTimeoutMS) {

        CloseableHttpClient httpclient = HttpClients.createDefault();

        return get(url,hearderList,connectTimeoutMS,socketTimeoutMS,httpclient);
    }


    public static String get(String url,  List<KV> hearderList, int connectTimeoutMS, int socketTimeoutMS, CloseableHttpClient httpclient) {

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

            logger.info("executing request " + httpGet.getURI());
            CloseableHttpResponse response = httpclient.execute(httpGet);
            try {
                entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                    logger.info("Response content: " + result);

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

    public static String getUrl(String urlString) {
        return getUrl(urlString, null,15000, 15000);
    }

    public static String getUrl(String urlString, List<KV> hearderList, int connectTimeoutMS, int readTimeoutMS) {
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);

            URLConnection conn = url.openConnection();

            conn.setConnectTimeout(connectTimeoutMS);
            conn.setReadTimeout(readTimeoutMS);

            if (hearderList != null) {
                for (KV kv : hearderList) {
                    conn.addRequestProperty(kv.getK(),kv.getV().toString());
                }
            }

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            for (String line = null; (line = reader.readLine()) != null; ) {
                sb.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        String result = "";
        try {
            result = URLDecoder.decode(sb.toString(), "UTF-8");
            logger.info("get: " + result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

        return result;
    }

    public final static String EQ = "=";
    public final static String AND = "&";



    public static String getUrl(String url, Map<String, String> map) {

        if (StringUtil.isNullOrEmpty(url))
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append("?");

        int size = map.size();
        int i = 0;
        for (String key : map.keySet()) {
            i++;
            String value = map.get(key);
            sb.append(key).append(EQ).append(value);
            if (i < size) {
                sb.append(AND);
            }
        }

        String requestStr = sb.toString();

        logger.info("get url: " + requestStr);

        String result = get(requestStr);

        return result;
    }

    public static void upload(String url, String filepath) {

    }

    public static FileWrapper getFile(String url) {

        FileWrapper file = new FileWrapper();
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();

            String extName = getFileExpandedName(http.getHeaderField("Content-Type"));
            file.setExtName(extName);
            // 获取文件转化为byte流
            is = http.getInputStream();

            byte[] data = new byte[10240];
            int len = 0;

            baos = new ByteArrayOutputStream();

            while ((len = is.read(data)) != -1) {
                baos.write(data, 0, len);
            }

            file.setBytes(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;

    }

    private static String getFileExpandedName(String contentType) {
        String fileEndWitsh = "";
        if ("image/jpeg".equals(contentType))
            fileEndWitsh = ".jpg";
        if ("image/png".equals(contentType))
            fileEndWitsh = ".png";
        else if ("audio/mpeg".equals(contentType))
            fileEndWitsh = ".mp3";
        else if ("audio/amr".equals(contentType))
            fileEndWitsh = ".amr";
        else if ("video/mp4".equals(contentType))
            fileEndWitsh = ".mp4";
        else if ("video/mpeg4".equals(contentType))
            fileEndWitsh = ".mp4";
        return fileEndWitsh;
    }

    public static class FileWrapper {
        private String extName;
        private byte[] bytes;

        public String getExtName() {
            return extName;
        }

        public void setExtName(String extName) {
            this.extName = extName;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public String toString() {
            return "FileWrapper [extName=" + extName + ", bytes=" + Arrays.toString(bytes) + "]";
        }
    }
}
