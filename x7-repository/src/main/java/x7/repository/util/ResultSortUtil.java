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
package x7.repository.util;

import x7.core.bean.BeanElement;
import x7.core.bean.Criteria;
import x7.core.bean.KV;
import x7.core.bean.Parsed;
import x7.core.util.ExceptionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultSortUtil {

    /**
     * by orderIn0
     * @param list
     * @param criteria
     * @param parsed
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public  static <T> void sort(List<T> list, Criteria criteria, Parsed parsed) {

        if (list.isEmpty())
            return;

        List<KV> fixedSortList = criteria.getFixedSortList();

        if (fixedSortList.isEmpty())
            return;

        KV kv0 = fixedSortList.get(0);

        List<T> tempList = new ArrayList<>();
        tempList.addAll(list);

        list.clear();

        String property = kv0.k;

        try {
            for (Object para : (List<Object>) kv0.v) {
                for (T result : tempList) {
                    BeanElement be = parsed.getElement(property);
                    Object o = be.getMethod.invoke(result);
                    if (String.valueOf(para).equals(String.valueOf(o))) {
                        list.add(result);
                    }
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

    }

    public static void sort(List<Map<String, Object>> list, Criteria.ResultMappedCriteria criteria) {

        if (list.isEmpty())
            return;

        List<KV> fixedSortList = criteria.getFixedSortList();

        if (fixedSortList.isEmpty())
            return;

        KV kv0 = fixedSortList.get(0);
        String property = kv0.k;

        Map<String,Object> test = list.get(0);
        if (!test.containsKey(property))
            return;

        List<Map<String, Object>> tempList = new ArrayList<>();
        tempList.addAll(list);

        list.clear();

        for (Object para : (List<Object>)kv0.v){
            for (Map<String,Object> mapResult: tempList){
                Object o = mapResult.get(property);
                if (String.valueOf(para).equals(String.valueOf(o))){
                    list.add(mapResult);
                }
            }
        }

    }
}
