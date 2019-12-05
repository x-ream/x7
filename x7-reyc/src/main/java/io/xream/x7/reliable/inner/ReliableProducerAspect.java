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
package io.xream.x7.reliable.inner;

import io.xream.x7.reliable.api.MessageTraceable;
import io.xream.x7.reliable.ReliableProducer;
import io.xream.x7.reliable.api.ReliableBackend;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import x7.core.exception.BusyException;
import x7.core.util.ExceptionUtil;

import java.util.concurrent.TimeUnit;


@Aspect
public class ReliableProducerAspect {


    private final static Logger logger = LoggerFactory.getLogger(ReliableBackend.class);

    public ReliableProducerAspect() {
        logger.info("Reliable Producer Enabled");
    }

    @Autowired
    private ReliableBackend backend;

    @Pointcut("@annotation(io.xream.x7.reliable.ReliableProducer))")
    public void cut() {

    }

    @Around("cut() && @annotation(reliableProducer) ")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, ReliableProducer reliableProducer) {

        long startTime = System.currentTimeMillis();

        Object[] args = proceedingJoinPoint.getArgs();
        Object body = null;
        for (Object arg : args) {
            if (arg.getClass() == reliableProducer.type()) {
                body = arg;
                break;
            }
        }

        Signature signature = proceedingJoinPoint.getSignature();
        String str = signature.getDeclaringTypeName() + "."+ signature.getName();

        if (body == null)
            throw new IllegalArgumentException(str + ", ReliableMessage arg can not be null, type: " + reliableProducer.type());

        MessageTraceable tracing = null;
        for (Object arg : args) {
            if (arg instanceof MessageTraceable) {
                tracing = (MessageTraceable) arg;
                break;
            }
        }

        int maxRetry = reliableProducer.retryMax();


        if (reliableProducer.useTcc()){
            maxRetry = maxRetry > 3 ? 3 : maxRetry;
        }else {
            maxRetry = maxRetry < 3 ? 3 : maxRetry;
        }

        final String msgId = MessageIdGenerator.get();

        String[] svcs = reliableProducer.svcs();
        for (String svc : svcs){
            if (svc.contains(",")) {
                throw new IllegalArgumentException(str + ", " + ReliableProducer.class.getName() + ", svcs: " + svcs);
            }
        }

        Object result = this.backend.produceReliably(
                reliableProducer.useTcc(),//
                msgId,//
                maxRetry,//
                reliableProducer.underConstruction(),//
                reliableProducer.topic(),//
                body,//
                tracing,//
                reliableProducer.svcs(),//
                () -> {
                    try {
                        MethodSignature ms = ((MethodSignature) signature);
                        if (ms.getReturnType() == void.class) {
                            proceedingJoinPoint.proceed();
                            return null;
                        } else {
                            return proceedingJoinPoint.proceed();
                        }
                    } catch (Throwable e) {
                        throw new RuntimeException(ExceptionUtil.getMessage(e));
                    }
                }
        );

        if (reliableProducer.async() && ! reliableProducer.useTcc())
            return result;

        boolean isOk = false;
        int maxReplay = 3;
        long duration = 15;
        int replay = 0;
        while (replay < maxReplay)
        {
            try {
                TimeUnit.MILLISECONDS.sleep(duration);
                isOk = this.backend.tryToConfirm(msgId);
                if (isOk) {
                  logger.info("handled OK time: {} ,replay = {} ,for {}" , System.currentTimeMillis() - startTime , replay ,proceedingJoinPoint.getSignature());
                  return result;
                }
                replay++;
            }catch (Exception e) {
                break;
            }
        }

        maxRetry = 6;
        duration = 1000;
        maxReplay = replay + maxRetry;
        while (replay < maxReplay)
        {
            try {
                TimeUnit.MILLISECONDS.sleep(duration);
                isOk = this.backend.tryToConfirm(msgId);
                if (isOk) {
                    logger.info("handled OK, time: {} ,replay = {} ,for {}" , System.currentTimeMillis() - startTime , replay ,proceedingJoinPoint.getSignature());
                    return result;
                }
                replay++;
            }catch (Exception e) {
                break;
            }
        }

        if (reliableProducer.useTcc()) {
            this.backend.cancel(msgId);
            logger.info("handled FAIL, time: {} ,replay = {} ,for {}" , System.currentTimeMillis() - startTime , replay ,proceedingJoinPoint.getSignature());
            throw new BusyException("TIMEOUT, X TRANSACTION UN FINISHED");
        }

        return result;
    }

}
