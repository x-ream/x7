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

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;
import io.xream.x7.reyc.BackendService;
import io.xream.x7.reyc.api.ReyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x7.core.exception.BusyException;
import x7.core.exception.RemoteServiceException;
import x7.core.exception.ReyConnectException;
import x7.core.util.StringUtil;

import java.util.function.Supplier;

public class R4JTemplate implements ReyTemplate {

    private static Logger logger = LoggerFactory.getLogger(ReyTemplate.class);

    private CircuitBreakerRegistry circuitBreakerRegistry;
    private RetryRegistry retryRegistry;

    private static ReyProperties reyProperties;

    public R4JTemplate(CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry, ReyProperties reyProperties) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
        this.reyProperties = reyProperties;
    }

    @Override
    public String support(String circuitBreakerKey, boolean isRetry, BackendService backendService) {

        if (StringUtil.isNullOrEmpty(circuitBreakerKey)){
            circuitBreakerKey = "";
        }

        final String backendName = circuitBreakerKey.equals("") ? "DEFAULT" : circuitBreakerKey;

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerKey);
        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, backendService::handle);

        if (isRetry) {
            Retry retry = retryRegistry.retry(circuitBreakerKey);
            if (retry != null) {

                retry.getEventPublisher()
                        .onRetry(event -> {
                            if (logger.isDebugEnabled()) {
                                logger.debug(event.getEventType().toString() + "_" + event.getNumberOfRetryAttempts() + ": backend("
                                        + backendName +")");
                            }
                        });

                decoratedSupplier = Retry
                        .decorateSupplier(retry, decoratedSupplier);
            }
        }

        String logStr = "Backend("+ backendName +")";

        String result = Try.ofSupplier(decoratedSupplier)
                .recover(e ->
                        hanleException(e, logStr, backendService)
                ).get();

        handleRemoteException(result,backendService);
        return result;
    }



    private String hanleException(Throwable e, String tag, BackendService backendService) {

        if (e instanceof CircuitBreakerOpenException) {
            if (logger.isErrorEnabled()) {
                logger.error(tag + ": " + e.getMessage());
            }
            Object obj = backendService.fallback();
            throw new BusyException(obj == null ? null : obj.toString());
        }

        String str = e.toString();
        if (str.contains("HttpHostConnectException")
                || str.contains("ConnectTimeoutException")
                || str.contains("ConnectException")
        ) {
            if (logger.isErrorEnabled()) {
                logger.error(tag + " : " + e.getMessage());
            }
            Object obj = backendService.fallback();
            throw new ReyConnectException(tag + " : " + e.getMessage() + (obj == null ? "" : (" : " + obj.toString())));
        }

        if (e instanceof RuntimeException) {
            if (logger.isErrorEnabled()) {
                logger.error(tag + " : " + e.getMessage());
            }
        }

        throw new RuntimeException(tag + " : " + e.getMessage());
    }


    private void handleRemoteException(String result,BackendService backendService) {

        if (result == null)
            return;

        if (result.contains(reyProperties.getRemoteException())){
            backendService.fallback();

            if (logger.isErrorEnabled()) {
                logger.error(result);
            }

            throw new RemoteServiceException(result);

        } else if (result.contains("RemoteServiceException")
                || result.contains("RuntimeException")
                || result.contains("BizException")
                || result.contains("BusyException")
                || result.contains("RollbackException")
                || result.contains("Internal Server Error")
                || result.contains(".Exception")
                || result.contains(".Throwable")
        ) {

            if (logger.isErrorEnabled()) {
                logger.error(result);
            }

            throw new RemoteServiceException(result);
        }

    }
}
