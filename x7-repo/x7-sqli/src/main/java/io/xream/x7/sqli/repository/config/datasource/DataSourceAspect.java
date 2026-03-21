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
package io.xream.x7.sqli.repository.config.datasource;

import io.xream.internal.util.ExceptionUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

/**
 * @author Sim
 */
@Aspect
public class DataSourceAspect implements Ordered {
    @Override
    public int getOrder() {
        return 0;
    }

    @Pointcut("@annotation(io.xream.x7.sqli.repository.config.datasource.ReadOnly))")
    public void cut() {

    }

    @Around("cut() && @annotation(readOnly) ")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, ReadOnly readOnly) {

        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature ms = ((MethodSignature) signature);
        try {
            DataSourceContextHolder.set(DataSourceType.READ);
            if (ms.getReturnType() == void.class) {
                proceedingJoinPoint.proceed();
                return null;
            } else {
                return proceedingJoinPoint.proceed();
            }
        } catch (Throwable e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }finally {
            DataSourceContextHolder.remove();
        }
    }

}
