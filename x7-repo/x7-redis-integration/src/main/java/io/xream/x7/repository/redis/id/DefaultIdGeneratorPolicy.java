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
package io.xream.x7.repository.redis.id;

import io.xream.x7.common.bean.*;
import io.xream.x7.common.repository.X;
import io.xream.x7.repository.BaseRepository;
import io.xream.x7.repository.id.IdGeneratorPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultIdGeneratorPolicy implements IdGeneratorPolicy {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public long createId(String clzName) {

        return this.stringRedisTemplate.opsForHash().increment(ID_MAP_KEY,clzName,1);

    }


    @Override
    public void onStart(List<BaseRepository> repositoryList) {

        if (repositoryList == null)
            return;

        for (BaseRepository baseRepository : repositoryList) {
            CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
            Class clzz = baseRepository.getClz();
            Parsed parsed = Parser.get(clzz);
            String key = parsed.getKey(X.KEY_ONE);
            BeanElement be = parsed.getElement(key);
            if (be.clz == String.class)
                continue;
            builder.reduce(ReduceType.MAX, be.property).paged().ignoreTotalRows();
            Criteria.ResultMappedCriteria resultMappedCriteria = builder.get();

            List<Long> idList = baseRepository.listPlainValue(Long.class,resultMappedCriteria);
            Long maxId = idList.stream().findFirst().orElse(0L);
            String name = baseRepository.getClz().getName();

            this.stringRedisTemplate.opsForHash().put(IdGeneratorPolicy.ID_MAP_KEY, name, String.valueOf(maxId));
        }

    }
}
