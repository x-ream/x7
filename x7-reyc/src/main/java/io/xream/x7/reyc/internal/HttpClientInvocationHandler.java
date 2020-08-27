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

import io.xream.x7.base.api.BackendService;
import io.xream.x7.base.api.HttpHeaderSupport;
import io.xream.x7.base.util.ExceptionUtil;
import io.xream.x7.base.util.LoggerProxy;
import io.xream.x7.reyc.api.SimpleResult;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Sim
 */
public class HttpClientInvocationHandler implements InvocationHandler {

    private HttpClientProxy httpClientProxy;

    public HttpClientInvocationHandler(HttpClientProxy httpClientProxy){
        this.httpClientProxy = httpClientProxy;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)  {
        final String methodName = method.getName();
        if (methodName.equals("toString"))
            return null;

        Class clzz = httpClientProxy.getObjectType();
        long startTime = System.currentTimeMillis();
        try{

            LoggerProxy.debug(clzz,methodName +"(..) start....");

            R r = HttpClientResolver.r(clzz.getName(),methodName,args);

            if (httpClientProxy.getBackend() == null) {
                SimpleResult result = HttpClientResolver.resolve(r,clzz);
                Object obj = HttpClientResolver.toObject(r.getReturnType(),r.getGeneType(),result.getBody());
                if (obj instanceof HttpHeaderSupport) {
                    HttpHeaderSupport hds = ((HttpHeaderSupport)obj);
                    hds.set(hds.getHeadName(),result.getHeaderMap().get(hds.getHeadName()));
                }
                return obj;
            }

            Map<String,String> headerMap = new HashMap<>();

            String result = HttpClientResolver.wrap(httpClientProxy, new BackendService<String>() {
                @Override
                public String handle() {
                    SimpleResult simpleResult = HttpClientResolver.resolve(r,clzz);
                    if (simpleResult.getHeaderMap() != null){
                        headerMap.putAll(simpleResult.getHeaderMap());
                    }
                    return simpleResult.getBody();
                }

                @Override
                public String fallback() {
                    return HttpClientResolver.fallback(clzz.getName(),methodName,args);
                }
            });

            Object obj = HttpClientResolver.toObject(r.getReturnType(),r.getGeneType(),result);
            if (obj instanceof HttpHeaderSupport) {
                HttpHeaderSupport hds = ((HttpHeaderSupport)obj);
                hds.set(hds.getHeadName(),headerMap.get(hds.getHeadName()));
            }
            return obj;

        } catch (RuntimeException re){
            throw re;
        } catch (Exception e){
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }finally {
            long endTime = System.currentTimeMillis();
            LoggerProxy.debug(clzz,methodName + "(..) end, cost time: " + (endTime - startTime) + "ms");
        }
    }
}
