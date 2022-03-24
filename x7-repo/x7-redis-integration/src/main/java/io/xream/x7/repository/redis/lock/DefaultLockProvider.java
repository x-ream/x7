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
package io.xream.x7.repository.redis.lock;

import io.xream.internal.util.ExceptionUtil;
import io.xream.internal.util.VerifyUtil;
import io.xream.x7.lock.DistributionLock;
import io.xream.x7.lock.LockProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DefaultLockProvider implements LockProvider {

    private final static Logger logger = LoggerFactory.getLogger(LockProvider.class);

    private RedisScript<Long> unLockScript = new RedisScript<Long>() {
        @Override
        public String getSha1() {
            return VerifyUtil.toMD5("x7-lock");
        }

        @Override
        public Class<Long> getResultType() {
            return Long.class;
        }

        @Override
        public String getScriptAsString() {
            return "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        }
    };

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean lock(String key, String value, @NotNull Integer timeOut){
        if (timeOut.intValue() == 0)
            timeOut = DEFAULT_TIMEOUT;
        try {
            return this.stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeOut, TimeUnit.MILLISECONDS);
        }catch (Exception e) {
            logger.error("DistributionLock.lock Exception: {}", ExceptionUtil.getMessage(e));
            return true;
        }
    }

    @Override
    public void unLock(DistributionLock.Lock lock){
        try {
            List<String> keys = new ArrayList<>();
            keys.add(lock.getKey());
            this.stringRedisTemplate.execute(unLockScript, keys, lock.getValue());
        }catch (Exception e){
            logger.error("DistributionLock.unlock Exception: {}", ExceptionUtil.getMessage(e));
        }
    }
}
