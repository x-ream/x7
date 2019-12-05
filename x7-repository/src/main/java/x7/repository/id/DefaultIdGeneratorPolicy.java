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
package x7.repository.id;

import org.springframework.data.redis.core.StringRedisTemplate;
import x7.repository.IdGenerator;

import java.util.List;

public class DefaultIdGeneratorPolicy implements IdGeneratorPolicy {


    private StringRedisTemplate stringRedisTemplate;
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public long createId(String clzName) {

        return this.stringRedisTemplate.opsForHash().increment(ID_MAP_KEY,clzName,1);

    }

    @Override
    public void onStart(List<IdGenerator> idGeneratorList) {

            System.out.println("\n" + "----------------------------------------");

            for (IdGenerator generator : idGeneratorList) {
                String name = generator.getClzName();
                long maxId = generator.getMaxId();

                String idInRedis = null;
                Object obj = this.stringRedisTemplate.opsForHash().get(IdGeneratorPolicy.ID_MAP_KEY, name);

                if (obj != null) {
                    idInRedis =  obj.toString().trim();
                }

                System.out.println(name + ",test, idInDB = " + maxId + ", idInRedis = " + idInRedis);


                if (idInRedis == null) {
                    this.stringRedisTemplate.opsForHash().put(IdGeneratorPolicy.ID_MAP_KEY, name, String.valueOf(maxId));
                } else if (idInRedis != null && maxId > Long.valueOf(idInRedis)) {
                    this.stringRedisTemplate.opsForHash().put(IdGeneratorPolicy.ID_MAP_KEY, name, String.valueOf(maxId));
                }

                System.out.println(name + ",final, idInRedis = " + this.stringRedisTemplate.opsForHash().get(IdGeneratorPolicy.ID_MAP_KEY, name));


            }
            System.out.println("----------------------------------------" + "\n");
    }
}
