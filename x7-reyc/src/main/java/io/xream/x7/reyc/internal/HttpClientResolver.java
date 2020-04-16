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

import io.xream.x7.api.BackendService;
import io.xream.x7.common.bean.KV;
import io.xream.x7.common.util.JsonX;
import io.xream.x7.common.util.StringUtil;
import io.xream.x7.reyc.ReyClient;
import io.xream.x7.reyc.Url;
import io.xream.x7.reyc.api.GroupRouter;
import io.xream.x7.reyc.api.ReyTemplate;
import io.xream.x7.reyc.api.SimpleRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HttpClientResolver {


    private static Logger logger = LoggerFactory.getLogger(ReyClient.class);

    private static ReyTemplate reyTemplate;

    private static SimpleRestTemplate restTemplate;

    public static void init( ReyTemplate rt, SimpleRestTemplate simpleRestTemplate) {
        reyTemplate = rt;
        restTemplate = simpleRestTemplate;
    }

    public static void setRestTemplate(SimpleRestTemplate simpleRestTemplate){
        restTemplate = simpleRestTemplate;
    }


    private static Pattern pattern = Pattern.compile("\\{[\\w]*\\}");

    protected static R r(String remoteIntfName, String methodName, Object[] args) {

        ClientParsed parsed = ClientParser.get(remoteIntfName);
        String url = parsed.getUrl();

        MethodParsed methodParsed = parsed.getMap().get(methodName);

        if (methodParsed == null)
            throw new RuntimeException("RequestMapping NONE: " + remoteIntfName + "." + methodName);

        String mapping = methodParsed.getRequestMapping();

        url = url + mapping;

        List<Object> objectList = new ArrayList<>();
        boolean flag = false;
        if (args != null) {
            for (Object arg : args) {
                if (arg != null && arg instanceof Url) {
                    Url dynamicUrl = (Url) arg;
                    url = dynamicUrl.value();
                    flag = true;
                } else {
                    objectList.add(arg);
                }
            }
        }
        if (flag) {
            args = objectList.toArray();
        }

        if (!url.startsWith("http")) {
            url = "http://" + url;
        }

        RequestMethod requestMethod = methodParsed.getRequestMethod();

        R r = new R();
        r.setArgs(args);
        r.setRequestMethod(requestMethod);
        r.setReturnType(methodParsed.getReturnType());
        r.setGeneType(methodParsed.getGeneType());
        r.setUrl(url);
        r.setHeaderList(methodParsed.getHeaderList());
        r.setRouter(parsed.getGroupRouter());
        return r;
    }

    protected static String resolve(R r, Class clz) {

        RequestMethod requestMethod = r.getRequestMethod();
        Object[] args = r.getArgs();
        String url = r.getUrl();
        List<KV> headerList = r.getHeaderList();

        GroupRouter router = r.getRouter();
        if (router != null){
            Object arg = null;
            if (args != null && args.length > 0) {
                arg = args[0];
            }
            url = url.replace(router.replaceHolder(),router.replaceValue(arg));
        }

        String result = null;
        if (requestMethod == RequestMethod.POST) {

            if (args != null && args.length > 0) {
                 result = restTemplate.post(clz,url,args[0],headerList);
            } else {
                result = restTemplate.post(clz,url,null,headerList);
            }
        } else {
            List<String> regExList = StringUtil.listByRegEx(url, pattern);
            int size = regExList.size();
            for (int i = 0; i < size; i++) {
                url = url.replace(regExList.get(i), args[i].toString());
            }
            result = restTemplate.get(clz,url,headerList);
        }

        if (StringUtil.isNullOrEmpty(result))
            return null;


        return result;
    }

    protected static Object toObject(Class<?> returnType, Class<?> geneType, String result) {

        if (result == null)
            return null;

        if (returnType == null || returnType == void.class) {
            return null;
        }

        if (returnType == Object.class)
            return result;

        if (returnType == List.class){
            return JsonX.toList(result,geneType);
        }

        Object obj = JsonX.toObject(result, returnType);

        return obj;
    }


    protected static String wrap(HttpClientProxy proxy, BackendService<String> backendService) {

        String result = reyTemplate.support(proxy.getBackend(),proxy.isRetry(),backendService);

        return result;
    }



    public static String fallback(String intfName, String methodName, Object[] args) {

        ClientParsed parsed = ClientParser.get(intfName);
        if (parsed.getFallback() == null)
            return null;
        Method method = parsed.getFallbackMethodMap().get(methodName);

        if (method == null)
            return null;

        try {
            if (method.getReturnType() == void.class) {
                method.invoke(parsed.getFallback(), args);
                return null;
            }
            Object obj = method.invoke(parsed.getFallback(), args);
            if (obj == null)
                return (String) obj;
            return obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception of fallback: " + intfName + "." + methodName);
        }

    }


}
