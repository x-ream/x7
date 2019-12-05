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
package x7.repository.cache;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import x7.core.repository.CacheableL3;
import x7.core.util.ExceptionUtil;
import x7.core.util.JsonX;
import x7.core.util.StringUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * if read data only from cache, and no data in cache return <br>
 * use @CacheableL3 instead of @Cacheable
 */
@Aspect
public class L3CacheAspect {

    @Value("${x7.cache.l3.expire-time:'6000ms'}")
    private String expireTime = "6000ms";

    private long getExpireTime(){
        expireTime = expireTime.toLowerCase();
        if (expireTime.contains("ms")){
            expireTime = expireTime.replace("ms","");
            return Long.valueOf(expireTime);
        }
        if (expireTime.contains("s")){
            expireTime = expireTime.replace("s","");
            return Long.valueOf(expireTime) * 1000;
        }
        if (expireTime.contains("m")){
            expireTime = expireTime.replace("m","");
            return Long.valueOf(expireTime) * 1000 * 60;
        }
        if (expireTime.contains("h")){
            expireTime = expireTime.replace("h","");
            return Long.valueOf(expireTime) * 1000 * 60 * 60;
        }
        if (expireTime.contains("d")){
            expireTime = expireTime.replace("d","");
            return Long.valueOf(expireTime) * 1000 * 60 * 60 * 24;
        }

        try {
            return Long.valueOf(expireTime);
        }catch (Exception e){
            return 6000;
        }
    }


    private ArgsToString argsToString;

    public void setArgsToString(ArgsToString argsToString) {
        this.argsToString = argsToString;
    }

    private L3CacheResolver resolver;

    public void setResolver(L3CacheResolver resolver) {
        this.resolver = resolver;
    }

    @Pointcut("@annotation(x7.core.repository.CacheableL3))")
    public void cut() {

    }

    @Around("cut() && @annotation(cacheableL3) ")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, CacheableL3 cacheableL3) {

        org.aspectj.lang.Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature ms = ((MethodSignature) signature);
        String methodName = ms.toShortString();
        Class returnType = ms.getReturnType();
        try {
            if (returnType == void.class) {
                proceedingJoinPoint.proceed();
                return null;
            }
        } catch (Throwable e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

        Object[] argArr = proceedingJoinPoint.getArgs();
        String key = argsToString.get(methodName, argArr);
        long expireTime = cacheableL3.expireTime();
        TimeUnit timeUnit = cacheableL3.timeUnit();

        if (expireTime == 0) {
            expireTime = getExpireTime();
            timeUnit = TimeUnit.MILLISECONDS;
        }

        String value = resolver.resolve(key, expireTime, timeUnit,
                () -> {
                    try {
                        return proceedingJoinPoint.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(ExceptionUtil.getMessage(e));
                    }
                });

        if (StringUtil.isNullOrEmpty(value))
            return null;
        if (returnType == List.class) {

            Type gt = ms.getMethod().getGenericReturnType();
            ParameterizedType pt = (ParameterizedType) gt;
            Type[] tt = pt.getActualTypeArguments();
            Type t = tt[0];
            if (t instanceof ParameterizedType) {
                return JSON.parseArray(value,tt);
            }

            Class genericClz = (Class) t;

            return JsonX.toList(value, genericClz);
        }
        return JsonX.toObject(value, returnType);
    }

}
