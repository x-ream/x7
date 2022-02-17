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
package io.xream.x7.reyc;

import io.xream.x7.reyc.internal.HttpProperties;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Import({HttpProperties.class})
public class RestTemplateConfig {


    public X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public SSLSocketFactory sslSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ConnectionPool pool(HttpProperties httpProperties) {
        return new ConnectionPool(httpProperties.getMaxIdleConnections(), httpProperties.getKeepAliveDuration(), TimeUnit.MILLISECONDS);
    }

    public OkHttpClient okHttpClient(HttpProperties httpProperties) {
        return new OkHttpClient.Builder()
//                .sslSocketFactory(sslSocketFactory(), x509TrustManager())
                .retryOnConnectionFailure(false)
                .connectionPool(pool(httpProperties))
                .connectTimeout(httpProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(httpProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(httpProperties.getWriteTimeout(),TimeUnit.MILLISECONDS)
                .build();
    }


    @Bean("restTemplate")
    public RestTemplate restTemplate(HttpProperties httpProperties) {
        OkHttpClient httpClient = okHttpClient(httpProperties);
        ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }
}