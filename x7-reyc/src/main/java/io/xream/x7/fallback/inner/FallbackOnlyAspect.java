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
package io.xream.x7.fallback.inner;

import io.xream.x7.common.util.ExceptionUtil;
import io.xream.x7.fallback.FallbackOnly;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aspect
public class FallbackOnlyAspect {


    private final static Logger logger = LoggerFactory.getLogger(FallbackOnly.class);

    public FallbackOnlyAspect() {
        logger.info("FallbackOnly Enabled");
    }


    @Pointcut("@annotation(io.xream.x7.fallback.FallbackOnly))")
    public void cut() {

    }

    @Around("cut() && @annotation(fallbackOnly) ")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, FallbackOnly fallbackOnly) {

        long startTime = System.currentTimeMillis();

        Object[] args = proceedingJoinPoint.getArgs();

        Signature signature = proceedingJoinPoint.getSignature();
        String logStr = signature.getDeclaringTypeName() + "." + signature.getName();

        if (args == null || args.length == 0)
            throw new IllegalArgumentException(logStr + ", @fallbackOnly not support no args' method");

        Class<? extends Throwable>[] clzzArr = fallbackOnly.exceptions();

        try {
            MethodSignature ms = ((MethodSignature) signature);
            if (ms.getReturnType() == void.class) {
                proceedingJoinPoint.proceed();
                return null;
            } else {
                return proceedingJoinPoint.proceed();
            }
        } catch (Throwable e) {

            for (Class<? extends Throwable> clzz : clzzArr) {
                if (e.getClass() == clzz || e.getClass().isAssignableFrom(clzz)) {
                    Class fallbackClzz = fallbackOnly.fallback();
                    if (fallbackClzz == void.class)
                        break;
                    try {
                        Object obj = fallbackClzz.newInstance();
                        String methodName = signature.getName();
                        Class[] clzzs= new Class[args.length];
                        for (int i=0; i<args.length; i++){
                            clzzs[i] = args[i].getClass();
                        }
                        fallbackClzz.getDeclaredMethod(methodName,clzzs).invoke(obj,args);
                    }catch (Exception ee){
                        e.printStackTrace();
                    }
                    break;
                }
            }

            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

    }

}
