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

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;
import io.xream.x7.api.BackendService;
import io.xream.x7.common.util.ExceptionUtil;
import io.xream.x7.common.util.StringUtil;
import io.xream.x7.exception.BusyException;
import io.xream.x7.exception.RemoteServiceException;
import io.xream.x7.exception.ReyConnectException;
import io.xream.x7.reyc.api.ReyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 *
 *  wrapped resilience4j: Retry,CircuitBreaker</br>
 *  Retry>CircuitBreaker>RateLimiter>Bulkhead  </br>
 *  but connection problem will retry immediately
 */
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
    public String support(String circuitBreakerKey, boolean isRetry, BackendService<String> backendService) {

        if (StringUtil.isNullOrEmpty(circuitBreakerKey)){
            circuitBreakerKey = "";
        }

        final String backendName = circuitBreakerKey.equals("") ? "default" : circuitBreakerKey;

        CircuitBreakerConfig circuitBreakerConfig = circuitBreakerRegistry.getConfiguration(backendName).orElse(circuitBreakerRegistry.getDefaultConfig());

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(backendName,circuitBreakerConfig);
        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, backendService::handle);

        if (isRetry) {
            RetryConfig retryConfig = retryRegistry.getConfiguration(backendName).orElse(retryRegistry.getDefaultConfig());
            Retry retry = retryRegistry.retry(backendName,retryConfig);
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



    private String hanleException(Throwable e, String tag, BackendService<String> backendService) {

        if (logger.isErrorEnabled()) {
            logger.error(tag + ": " + e.getMessage());
        }

        if (e instanceof CallNotPermittedException) {
            Object obj = backendService.fallback();
            throw new BusyException(obj == null ? null : obj.toString());
        }

        String str = e.toString();
        if (str.contains("HttpHostConnectException")
                || str.contains("ConnectTimeoutException")
                || str.contains("ConnectException")
                || str.contains("UnknownHostException")
                || str.contains("IOException")
        ) {
            Object obj = backendService.fallback();
            throw new ReyConnectException(tag + " : " + e.getMessage() + (obj == null ? "" : (" : " + obj.toString())));
        }

        throw new RuntimeException(tag + " : " + ExceptionUtil.getMessage(e));
    }


    private void handleRemoteException(String result,BackendService<String> backendService) {

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
