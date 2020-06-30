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
package io.xream.x7.repository.redis.cache;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import io.xream.x7.api.BackendService;
import io.xream.x7.common.cache.L2CacheStorage;
import io.xream.x7.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public final class DefaultL2CacheStorage implements L2CacheStorage {

    private final static Logger logger = LoggerFactory.getLogger(DefaultL2CacheStorage.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private L2CacheStorage fallbackStorage;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${circuitbreaker.l2cache.name:l2cache}")
    private String circuitBreakerL2cacheName;

    private CircuitBreakerConfig circuitBreakerConfig = null;

    @Autowired
    public void setFallbackStorage(L2CacheStorage l2CacheStorage) {
        this.fallbackStorage = l2CacheStorage;
    }

    public <T> T handle(BackendService<T> backendService) {

        if (this.circuitBreakerConfig == null) {
            this.circuitBreakerConfig = circuitBreakerRegistry.getConfiguration(circuitBreakerL2cacheName)
                    .orElse(circuitBreakerRegistry.getDefaultConfig());
        }
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerL2cacheName,this.circuitBreakerConfig);
        Supplier<T> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, backendService::handle);
        if (fallbackStorage == null)
            return Try.ofSupplier(decoratedSupplier).get();
        return Try.ofSupplier(decoratedSupplier).recover(e -> handleException(e,backendService)).get();
    }


    private <T> T handleException(Throwable e, BackendService<T> backendService){
        logger.info(ExceptionUtil.getMessage(e));
        return backendService.fallback();
    }

    public boolean set(String key, String value) {

        return this.handle(new BackendService<Boolean>() {
            @Override
            public Boolean handle() {
                if (key == null || key.equals(""))
                    return false;
                stringRedisTemplate.opsForValue().set(key, value);
                return true;
            }

            @Override
            public Boolean fallback() {
                if (fallbackStorage != null)
                    return fallbackStorage.set(key,value);
                return null;
            }
        });

    }


    public boolean set(String key, String value, int validSeconds, TimeUnit timeUnit) {
        return this.handle(new BackendService<Boolean>() {
            @Override
            public Boolean handle() {
                if (key == null || key.equals(""))
                    return false;
                stringRedisTemplate.opsForValue().set(key, value, validSeconds, timeUnit);
                return true;
            }

            @Override
            public Boolean fallback() {
                if (fallbackStorage != null)
                    return fallbackStorage.set(key,value,validSeconds,timeUnit);
                return null;
            }
        });
    }


    public String get(String key) {

        return this.handle(new BackendService<String>() {
            @Override
            public String handle() {
                String str = stringRedisTemplate.opsForValue().get(key);
                if (str == null)
                    return null;
                return str.trim();
            }

            @Override
            public String fallback() {
                if (fallbackStorage != null)
                    return fallbackStorage.get(key);
                return null;
            }
        });

    }


    public List<String> multiGet(List<String> keyList) {

		return this.handle(new BackendService<List<String>>() {
            @Override
            public List<String> handle() {
                if (keyList == null || keyList.isEmpty())
                    return null;

                return stringRedisTemplate.opsForValue().multiGet(keyList);
            }

            @Override
            public List<String> fallback() {
                if (fallbackStorage != null)
                    return fallbackStorage.multiGet(keyList);
                return null;
            }
        });

    }


    public boolean delete(String key) {
		return this.handle(new BackendService<Boolean>() {
            @Override
            public Boolean handle() {
                return stringRedisTemplate.delete(key);
            }

            @Override
            public Boolean fallback() {
                if (fallbackStorage != null)
                    return fallbackStorage.delete(key);
                return null;
            }
        });
    }

    public Set<String> keys(String pattern) {
		return this.handle(new BackendService<Set<String>>() {
            @Override
            public Set<String> handle() {
                return stringRedisTemplate.keys(pattern);
            }

            @Override
            public Set<String> fallback() {
                if (fallbackStorage != null)
                    return fallbackStorage.keys(pattern);
                return null;
            }
        });
    }

}
