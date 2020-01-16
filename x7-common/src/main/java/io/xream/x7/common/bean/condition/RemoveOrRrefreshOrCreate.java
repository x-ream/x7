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
package io.xream.x7.common.bean.condition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.xream.x7.common.bean.ConjunctionAndOtherScript;
import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.PredicateAndOtherScript;
import io.xream.x7.common.bean.Routeable;
import io.xream.x7.common.util.StringUtil;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;


public class RemoveOrRrefreshOrCreate<T> implements Routeable {

    private Object routeKey;
    @JsonIgnore
    private transient Class clz;

    private List<T> list;
    private List<Criteria.X> conditionList = new ArrayList<>();

    public RemoveOrRrefreshOrCreate(){}
    public RemoveOrRrefreshOrCreate(List<T> list){
        this.list = list;
    }

    @Override
    public Object getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(Object routeKey) {
        this.routeKey = routeKey;
    }

    public Class getClz() {
        return clz;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public List<Criteria.X> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<Criteria.X> conditionList) {
        this.conditionList = conditionList;
    }

    public RemoveOrRrefreshOrCreate eq(String property, Object value) {
        Assert.notNull(property, " RemoveOrRrefreshOrCreate.eq(property,vlaue), preperty can not null");
        Assert.notNull(property, " RemoveOrRrefreshOrCreate.eq(property,vlaue), value can not null");
        String str = String.valueOf(value);
        if (StringUtil.isNullOrEmpty(str) || str.equals("0"))
            throw new IllegalArgumentException("RemoveOrRrefreshOrCreate.eq(property,vlaue), value = " + value);

        Criteria.X x = new Criteria.X();
        x.setConjunction(ConjunctionAndOtherScript.AND);
        x.setPredicate(PredicateAndOtherScript.EQ);
        x.setKey(property);
        x.setValue(value);
        this.conditionList.add(x);
        return this;
    }

    public RemoveOrRrefreshOrCreate wrap(List<T> list){
        this.list = list;
        return this;
    }
}
