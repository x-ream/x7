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
import io.xream.sqli.spi.L2CacheStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class DefaultL2CacheStorage implements L2CacheStorage {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${circuitbreaker.l2cache.name:l2cache}")
    private String circuitBreakerL2cacheName;

    private CircuitBreakerConfig circuitBreakerConfig = null;

    public void setCircuitBreakerRegistry(CircuitBreakerRegistry registry) {
        this.circuitBreakerRegistry = registry;
    }


    public <T> T handle(BackendService<T> backendService) {

        if (this.circuitBreakerConfig == null) {
            this.circuitBreakerConfig = circuitBreakerRegistry.getConfiguration(circuitBreakerL2cacheName)
                    .orElse(circuitBreakerRegistry.getDefaultConfig());
        }
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerL2cacheName,this.circuitBreakerConfig);
        Supplier<T> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, backendService::handle);
        return Try.ofSupplier(decoratedSupplier).get();
    }



    public boolean set(String key, String value) {

        if (circuitBreakerRegistry == null) {
            if (key == null || key.equals(""))
                return false;
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        }

        return this.handle(new BackendService<Boolean>() {
            @Override
            public Boolean handle() {
                if (key == null || key.equals(""))
                    return false;
                stringRedisTemplate.opsForValue().set(key, value);
                return true;
            }

            @Override
            public Boolean fallback(Throwable e) {

                return true;
            }
        });

    }


    public boolean set(String key, String value, int validSeconds, TimeUnit timeUnit) {

        if (circuitBreakerRegistry == null) {
            if (key == null || key.equals(""))
                return false;
            stringRedisTemplate.opsForValue().set(key, value, validSeconds, timeUnit);
            return true;
        }

        return this.handle(new BackendService<Boolean>() {
            @Override
            public Boolean handle() {
                if (key == null || key.equals(""))
                    return false;
                stringRedisTemplate.opsForValue().set(key, value, validSeconds, timeUnit);
                return true;
            }

            @Override
            public Boolean fallback(Throwable e) {
                return true;
            }
        });
    }


    public String get(String key) {

        if (circuitBreakerRegistry == null) {
            String str = stringRedisTemplate.opsForValue().get(key);
            if (str == null)
                return null;
            return str.trim();
        }

        return this.handle(new BackendService<String>() {
            @Override
            public String handle() {
                String str = stringRedisTemplate.opsForValue().get(key);
                if (str == null)
                    return null;
                return str.trim();
            }

            @Override
            public String fallback(Throwable e) {
                return null;
            }
        });

    }


    public List<String> multiGet(List<String> keyList) {

        if (circuitBreakerRegistry == null) {
            if (keyList == null || keyList.isEmpty())
                return null;

            return stringRedisTemplate.opsForValue().multiGet(keyList);
        }

		return this.handle(new BackendService<List<String>>() {
            @Override
            public List<String> handle() {
                if (keyList == null || keyList.isEmpty())
                    return null;

                return stringRedisTemplate.opsForValue().multiGet(keyList);
            }

            @Override
            public List<String> fallback(Throwable e) {
                return null;
            }
        });

    }


    public boolean delete(String key) {
        if (circuitBreakerRegistry == null) {
            return stringRedisTemplate.delete(key);
        }
		return this.handle(new BackendService<Boolean>() {
            @Override
            public Boolean handle() {
                return stringRedisTemplate.delete(key);
            }

            @Override
            public Boolean fallback(Throwable e) {
                return null;
            }
        });
    }

    public Set<String> keys(String pattern) {

        if (circuitBreakerRegistry == null) {
            return stringRedisTemplate.keys(pattern);
        }

		return this.handle(new BackendService<Set<String>>() {
            @Override
            public Set<String> handle() {
                return stringRedisTemplate.keys(pattern);
            }

            @Override
            public Set<String> fallback(Throwable e) {
                return null;
            }
        });
    }

    public interface BackendService<T> {
        T handle();
        Object fallback(Throwable e) throws Throwable;
    }
}
