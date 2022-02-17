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

import io.xream.x7.base.KV;
import io.xream.x7.base.util.JsonX;
import io.xream.x7.base.util.LoggerProxy;
import io.xream.x7.base.util.StringUtil;
import io.xream.x7.reyc.api.ClientHeaderInterceptor;
import io.xream.x7.reyc.api.SimpleRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sim
 */
public class DefaultRestTemplate implements SimpleRestTemplate {

    private RestTemplate restTemplate;

    private List<ClientHeaderInterceptor> clientHeaderInterceptorList = new ArrayList<>();

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void headerInterceptor(ClientHeaderInterceptor clientHeaderInterceptor) {
        this.clientHeaderInterceptorList.add(clientHeaderInterceptor);
    }

    @Override
    public String post(Class clz, String url, Object requestObject, List<KV> headerList) {
        return execute(clz, url, requestObject, headerList, HttpMethod.POST);
    }

    @Override
    public String get(Class clz, String url, List<KV> headerList) {
        return execute(clz, url, null, headerList, HttpMethod.GET);
    }

    private String execute(Class clz, String url, Object request, List<KV> headerList, HttpMethod method) {
        // build http headers

        StringBuilder headerStr = new StringBuilder(" -H Content-type:application/json");
        HttpHeaders headers = new HttpHeaders();
        if (headerList != null) {
            for (KV kv : headerList) {
                headers.add(kv.k, String.valueOf(kv.v));
                if (!kv.getK().contains("Content-type")) {
                    headerStr.append(" -H ").append(kv.getK()).append(":").append(kv.getV());
                }
            }
        }

        for (ClientHeaderInterceptor headerInterceptor : clientHeaderInterceptorList) {
            KV kv = headerInterceptor.apply();
            if (kv == null)
                continue;
            headers.add(kv.k, String.valueOf(kv.v));
            if (!kv.getK().contains("Content-type")) {
                headerStr.append(" -H ").append(kv.getK()).append(":").append(kv.getV());
            }
        }

        // check content type
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        String json = request == null ? "" : JsonX.toJson(request);

        LoggerProxy.info(clz, "-X " + method.name() + "  " + url + headerStr + (StringUtil.isNotNull(json) ? (" -d '" + json + "'"):""));

        if (this.restTemplate == null)
            throw new NullPointerException(RestTemplate.class.getName());

        return restTemplate.exchange(url, method, new HttpEntity<>(json, headers), String.class).getBody();
    }


}
