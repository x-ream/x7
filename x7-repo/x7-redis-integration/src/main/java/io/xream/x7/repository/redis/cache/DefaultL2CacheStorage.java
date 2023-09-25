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


import io.xream.sqli.spi.L2CacheStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class DefaultL2CacheStorage implements L2CacheStorage {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public boolean set(String key, String value) {
        if (key == null || key.equals(""))
            return false;
        stringRedisTemplate.opsForValue().set(key, value);
        return true;
    }


    public boolean set(String key, String value, int validSeconds, TimeUnit timeUnit) {
        if (key == null || key.equals(""))
            return false;
        stringRedisTemplate.opsForValue().set(key, value, validSeconds, timeUnit);
        return true;
    }


    public String get(String key) {
        String str = stringRedisTemplate.opsForValue().get(key);
        if (str == null)
            return null;
        return str.trim();
    }

    public List<String> multiGet(List<String> keyList) {
        if (keyList == null || keyList.isEmpty())
            return null;
        return stringRedisTemplate.opsForValue().multiGet(keyList);
    }


    public boolean delete(String key) {
        if (key == null || key.equals(""))
            return false;
        return stringRedisTemplate.delete(key);
    }

    public Set<String> keys(String pattern) {
        if (pattern == null || pattern.equals(""))
            return null;
        return stringRedisTemplate.keys(pattern);
    }

}
