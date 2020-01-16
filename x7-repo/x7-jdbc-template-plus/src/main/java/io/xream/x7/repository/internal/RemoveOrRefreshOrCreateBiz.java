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

import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.Parsed;
import io.xream.x7.common.bean.Parser;
import io.xream.x7.common.bean.condition.RemoveOrRrefreshOrCreate;
import io.xream.x7.common.repository.X;
import io.xream.x7.common.util.ExceptionUtil;
import io.xream.x7.repository.KeyOne;
import io.xream.x7.repository.Repository;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RemoveOrRefreshOrCreateBiz {

    public static <T> boolean doIt(Class<T> clz, Repository repository, RemoveOrRrefreshOrCreate wrapper) {

        Assert.notNull(wrapper, "removeOrRefreshOrCreate(wrapper),wrapper is null");

        List<T> list = wrapper.getList();
        if (list == null || list.isEmpty())
            return false;

        List<Criteria.X> conditionList = wrapper.getConditionList();
        if (conditionList == null || conditionList.isEmpty())
            throw new IllegalArgumentException("removeOrRefreshOrCreate(wrapper),wrapper.conditionList is null");

        Criteria criteria = new Criteria();
        criteria.setClz(clz);
        criteria.reset(conditionList);

        List<T> existList = repository.list(criteria);

        final Parsed parsed = Parser.get(clz);
        Field f = parsed.getKeyField(X.KEY_ONE);
        Map<Object,Object> newMap = new HashMap<>();
        try {
            for (T t : list) {
                Object id = f.get(t);
                newMap.put(id, t);
            }
        }catch (Exception e){
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

        Map<Object,Object> existMap = new HashMap<>();
        try {
            for (T t : existList) {
                Object id = f.get(t);
                existMap.put(id, t);
            }
        }catch (Exception e){
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

        /*
         * remove
         */
        Iterator<Object> existIte = existMap.keySet().iterator();
        while (existIte.hasNext()) {
            Object id = existIte.next();
            if (newMap.containsKey(id))
                continue;
            existIte.remove();
            repository.remove(new KeyOne<Object>() {
                @Override
                public Object get() {
                    return id;
                }

                @Override
                public Class<Object> getClzz() {
                    return parsed.getClz();
                }
            });
        }

        TODO:
        /*
         * refresh
         */

        /*
         * create
         */


        return true;
    }
}
