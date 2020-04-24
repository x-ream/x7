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
package io.xream.x7.repository.internal;

import io.xream.x7.common.async.CasualWorker;
import io.xream.x7.repository.ManuRepository;
import io.xream.x7.repository.id.IdGenerator;


public final class GlobalIdPersistencePolicy {

    protected static void persist(long id, String clzName) {
        CasualWorker.accept(() -> {
            IdGenerator generator = new IdGenerator();
            generator.setClzName(clzName);
            generator.setMaxId(id);
            StringBuilder sb = new StringBuilder();
            sb.append("update idGenerator set maxId = ").append(id).append(" where clzName = '").append(clzName)
                    .append("' and ").append(id).append(" > maxId ;");//sss

            try {
                ManuRepository.execute(generator, sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
