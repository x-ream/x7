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
import io.xream.x7.common.cache.L2CacheStorage;
import io.xream.x7.repository.redis.inner.BackendService;
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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${circuitbreaker.l2cache.name:l2cache}")
    private String circuitBreakerL2cacheName;

    private CircuitBreakerConfig circuitBreakerConfig = null;


    public <T> T handle(BackendService<T> backendService) {

        if (this.circuitBreakerConfig == null) {
            this.circuitBreakerConfig = circuitBreakerRegistry.getConfiguration(circuitBreakerL2cacheName)
                    .orElse(circuitBreakerRegistry.getDefaultConfig());
        }
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerL2cacheName,this.circuitBreakerConfig);
        Supplier<T> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, backendService::handle);
        T t = Try.ofSupplier(decoratedSupplier).get();
        return t;
    }


    public boolean set(String key, String value) {

        return this.handle(() -> {
            if (key == null || key.equals(""))
                return false;
            this.stringRedisTemplate.opsForValue().set(key, value);
            return true;
        });

    }


    public boolean set(String key, String value, int validSeconds, TimeUnit timeUnit) {
        return this.handle(() -> {
            if (key == null || key.equals(""))
                return false;
            this.stringRedisTemplate.opsForValue().set(key, value, validSeconds, timeUnit);
            return true;
        });
    }


    public String get(String key) {

        return this.handle(() -> {
            String str = this.stringRedisTemplate.opsForValue().get(key);
            if (str == null)
                return str;
            return str.trim();
        });

    }


    public List<String> multiGet(List<String> keyList) {

		return this.handle(() -> {

			if (keyList == null || keyList.isEmpty())
				return null;

			List<String> list = this.stringRedisTemplate.opsForValue().multiGet(keyList);
			if (list == null)
				return null;
			return list;
		});

    }


    public boolean delete(String key) {
		return this.handle(() -> {
			this.stringRedisTemplate.delete(key);
			return true;
		});
    }

    public Set<String> keys(String pattern) {
		return this.handle(() -> {
			Set<String> set = this.stringRedisTemplate.keys(pattern);

			return set;
		});
    }

}
