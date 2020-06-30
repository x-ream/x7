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
import io.xream.x7.common.bean.KV;
import io.xream.x7.reyc.api.HeaderInterceptor;
import io.xream.x7.reyc.api.SimpleRestTemplate;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.ArrayList;
import java.util.List;

public class DefaultRestTemplate implements SimpleRestTemplate {

    private BraveHttpRequestInterceptor requestInterceptor;
    private BraveHttpResponseInterceptor responseInterceptor;
    private HttpProperties properties;

    private List<HeaderInterceptor> headerInterceptorList = new ArrayList<>();
    public void add(HeaderInterceptor headerInterceptor) {
        this.headerInterceptorList.add(headerInterceptor);
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

    public KV header(String key, String value){
        return new KV(key, value);
    }

    @Override
    public String post(Class clz, String url, Object request, List<KV> headerList) {

        CloseableHttpClient httpclient = null;
        if (requestInterceptor != null && responseInterceptor != null) {
            httpclient = httpClient(requestInterceptor, responseInterceptor);
        } else {
            httpclient = HttpClients.createDefault();
        }

        List<KV> tempHeaderList = new ArrayList<>();
        for (HeaderInterceptor headerInterceptor : headerInterceptorList){
            KV kv = headerInterceptor.apply(this);
            tempHeaderList.add(kv);
        }

        if (headerList!=null && !tempHeaderList.isEmpty()) {
            tempHeaderList.addAll(headerList);
        }
        String result = HttpClientUtil.post(clz,url,request,tempHeaderList,properties.getConnectTimeout(),properties.getSocketTimeout(),httpclient);

        return result;
    }

    @Override
    public String get(Class clz, String url, List<KV> headerList) {

        CloseableHttpClient httpclient = null;
        if (requestInterceptor != null && responseInterceptor != null) {
            httpclient = httpClient(requestInterceptor, responseInterceptor);
        } else {
            httpclient = HttpClients.createDefault();
        }
        List<KV> tempHeaderList = new ArrayList<>();
        for (HeaderInterceptor headerInterceptor : headerInterceptorList){
            KV kv = headerInterceptor.apply(this);
            tempHeaderList.add(kv);
        }

        if (headerList!=null && !tempHeaderList.isEmpty()) {
            tempHeaderList.addAll(headerList);
        }
        return HttpClientUtil.get(clz,url, tempHeaderList,properties.getConnectTimeout(),properties.getSocketTimeout(),httpclient);
    }


    private  CloseableHttpClient httpClient(BraveHttpRequestInterceptor requestInterceptor,
                                                 BraveHttpResponseInterceptor responseInterceptor) {
        CloseableHttpClient httpclient = HttpClients.custom()
                .addInterceptorFirst(requestInterceptor)
                .addInterceptorFirst(responseInterceptor).build();
        return httpclient;
    }

}
