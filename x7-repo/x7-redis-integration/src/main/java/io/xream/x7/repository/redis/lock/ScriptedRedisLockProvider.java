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

import io.xream.x7.lock.LockProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.List;

//@Component
public class ScriptedRedisLockProvider implements LockProvider {

    final String value = "~LOCK";
    final String lockScript = "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 then  redis.call('expire',KEYS[1],ARGV[2])  return 1 else  return 0 end";
//    final String lockScript = "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 then if redis.call('get',KEYS[1]) == ARGV[1] then  redis.call('expire',KEYS[1],ARGV[2]) return 1 else return 0 end else return 0 end";
    final String unLockScript = "return redis.call('del', KEYS[1])";
//    String unLockScript = "";


    @Autowired
    private RedisTemplate redisTemplate;

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean lock(String key, int timeout) {


        RedisScript<Object> redisScript = new DefaultRedisScript<>(lockScript, Object.class);

        timeout = timeout / 1000;
        Object obj =  redisTemplate.execute(redisScript,Collections.singletonList(key), value,timeout);
        List list = (List)obj;
        System.out.println(list.get(0));
        return list.get(0).toString().equals("1");

    }


    @Override
    public void unLock(String key) {

        RedisScript<Object> redisScript = new DefaultRedisScript<>(unLockScript, Object.class);
        Object obj =  redisTemplate.execute(redisScript,Collections.singletonList(key), value);
        List list = (List)obj;
        System.out.println(list.get(0));

    }
}
