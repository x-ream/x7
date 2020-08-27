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

import com.github.kristofa.brave.httpclient.BraveHttpRequestInterceptor;
import com.github.kristofa.brave.httpclient.BraveHttpResponseInterceptor;
import io.xream.x7.base.KV;
import io.xream.x7.reyc.api.HeaderRequestInterceptor;
import io.xream.x7.reyc.api.HeaderResponseInterceptor;
import io.xream.x7.reyc.api.SimpleRestTemplate;
import io.xream.x7.reyc.api.SimpleResult;
import org.apache.http.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Sim
 */
public class DefaultRestTemplate implements SimpleRestTemplate {

    private BraveHttpRequestInterceptor requestInterceptor;
    private BraveHttpResponseInterceptor responseInterceptor;
    private HttpProperties properties;

    private List<HeaderRequestInterceptor> headerRequestInterceptorList = new ArrayList<>();
    private List<HeaderResponseInterceptor> headerResponseInterceptorList = new ArrayList<>();
    public void add(HeaderRequestInterceptor headerInterceptor) {
        this.headerRequestInterceptorList.add(headerInterceptor);
    }
    public void add(HeaderResponseInterceptor headerResponseInterceptor) {
        this.headerResponseInterceptorList.add(headerResponseInterceptor);
    }

    public DefaultRestTemplate(){
    }

    public DefaultRestTemplate(
            HttpProperties properties,
            BraveHttpRequestInterceptor requestInterceptor,
            BraveHttpResponseInterceptor responseInterceptor){
        this.properties = properties;
        this.requestInterceptor = requestInterceptor;
        this.responseInterceptor = responseInterceptor;
    }


    public BraveHttpRequestInterceptor getRequestInterceptor() {
        return requestInterceptor;
    }

    public void setRequestInterceptor(BraveHttpRequestInterceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor;
    }

    public BraveHttpResponseInterceptor getResponseInterceptor() {
        return responseInterceptor;
    }

    public void setResponseInterceptor(BraveHttpResponseInterceptor responseInterceptor) {
        this.responseInterceptor = responseInterceptor;
    }


    @Override
    public SimpleResult post(Class clz, String url, Object request, List<KV> headerList) {

        HttpClientBuilder builder = builder();
        if (requestInterceptor != null && responseInterceptor != null) {

            builder.addInterceptorFirst(requestInterceptor)
                    .addInterceptorFirst(responseInterceptor);
        }
        builder.addInterceptorFirst((HttpRequestInterceptor) (httpRequest, httpContext) -> {
            if (headerList != null) {
                for (KV kv1 : headerList) {
                    httpRequest.addHeader(kv1.k, String.valueOf(kv1.v));
                }
            }
            for (HeaderRequestInterceptor headerInterceptor : headerRequestInterceptorList){
                KV kv = headerInterceptor.apply();
                httpRequest.addHeader(kv.k,String.valueOf(kv.v));
            }
        });

        Map<String,String> responseHeaderMap = new HashMap<>();

        builder.addInterceptorFirst((HttpResponseInterceptor) (httpResponse, httpContext) -> {
            for (HeaderResponseInterceptor headerResponseInterceptor : headerResponseInterceptorList) {
                Header header = httpResponse.getFirstHeader(headerResponseInterceptor.getKey());
                responseHeaderMap.put(header.getName(),header.getValue());
            }
        });

        CloseableHttpClient httpclient  = builder.build();

        String body = HttpClientUtil.post(clz,url,request,properties.getConnectTimeout(),properties.getSocketTimeout(),httpclient);

        return new SimpleResult(body,responseHeaderMap);
    }

    @Override
    public SimpleResult get(Class clz, String url, List<KV> headerList) {

        HttpClientBuilder builder = builder();
        if (requestInterceptor != null && responseInterceptor != null) {

            builder.addInterceptorFirst(requestInterceptor)
                    .addInterceptorFirst(responseInterceptor);
        }
        builder.addInterceptorFirst((HttpRequestInterceptor) (httpRequest, httpContext) -> {
            if (headerList != null) {
                for (KV kv1 : headerList) {
                    httpRequest.addHeader(kv1.k, String.valueOf(kv1.v));
                }
            }
            for (HeaderRequestInterceptor headerInterceptor : headerRequestInterceptorList){
                KV kv = headerInterceptor.apply();
                httpRequest.addHeader(kv.k,String.valueOf(kv.v));
            }
        });

        Map<String,String> responseHeaderMap = new HashMap<>();

        builder.addInterceptorFirst((HttpResponseInterceptor) (httpResponse, httpContext) -> {
            for (HeaderResponseInterceptor headerResponseInterceptor : headerResponseInterceptorList) {
                Header header = httpResponse.getFirstHeader(headerResponseInterceptor.getKey());
                responseHeaderMap.put(header.getName(),header.getValue());
            }
        });

        CloseableHttpClient httpclient =  builder.build();

        String body = HttpClientUtil.get(clz,url, properties.getConnectTimeout(),properties.getSocketTimeout(),httpclient);

        return new SimpleResult(body,responseHeaderMap);
    }


    private  HttpClientBuilder builder() {
        HttpClientBuilder builder = HttpClients.custom();
        return builder;
    }

}
