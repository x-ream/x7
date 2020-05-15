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
import io.xream.x7.common.bean.*;
import io.xream.x7.common.repository.X;
import io.xream.x7.common.util.BeanUtilX;
import io.xream.x7.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class RefreshCondition<T> implements CriteriaCondition, Routeable {

    private List<Criteria.X> refreshList = new ArrayList<>();
    private List<Criteria.X> listX = new ArrayList<>();
    private String sourceStript;//FIXME

    private Object routeKey;
    @JsonIgnore
    private transient Class clz;
    @JsonIgnore
    private transient List<Object> valueList = new ArrayList<>();


    public Class getClz() {
        return clz;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }

    public List<Criteria.X> getRefreshList() {
        return refreshList;
    }


    public String getSourceStript() {
        return sourceStript;
    }

    public void setSourceStript(String sourceStript) {
        this.sourceStript = sourceStript;
    }

    @Override
    public List<Criteria.X> getListX() {
        return listX;
    }

    @Override
    public List<Object> getValueList() {
        return this.valueList;
    }


    @Override
    public Map<String, String> getAliaMap() {
        return null;
    }


    @Override
    public Object getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(Object routeKey) {
        this.routeKey = routeKey;
    }

    @Deprecated
    public RefreshCondition(){
    }


    public static RefreshCondition build(){
        return new RefreshCondition();
    }


    @Deprecated
    public RefreshCondition  and(){
        return this;
    }

    /**
     *
     * String sqlX = "propertyA = propertyA + propertyB + 1"
     * @return RefreshCondition
     */
    public RefreshCondition refresh(String sqlX){

        if (Objects.isNull(sqlX))
            return this;

        Criteria.X x = new Criteria.X();
        x.setPredicate(PredicateAndOtherScript.X);
        x.setKey(sqlX);
        this.refreshList.add(x);

        return this;
    }

    public RefreshCondition refresh(String property, Object value){

        if (Objects.isNull(value))
            return this;

        Criteria.X x = new Criteria.X();
        x.setPredicate(PredicateAndOtherScript.EQ);
        x.setKey(property);
        x.setValue(value);
        this.refreshList.add(x);

        return this;
    }

    public KV tryToGetKeyOne() {
        if (clz == null)
            return null;
        Parsed parsed = Parser.get(clz);
        String keyOne = parsed.getKey(X.KEY_ONE);
        for (Criteria.X x : listX) {
            String key = x.getKey();
            if (key != null && key.equals(keyOne)) {
                return new KV(key,x.getValue());
            }
        }
        return null;
    }

    private RefreshCondition doGle(PredicateAndOtherScript p, String property, Object value) {
        if (value == null)
            return this;

        if (StringUtil.isNullOrEmpty(value))
            return this;

        Criteria.X x = new Criteria.X();
        x.setConjunction(ConjunctionAndOtherScript.AND);
        x.setPredicate(p);
        x.setKey(property);
        x.setValue(value);
        this.listX.add(x);
        return this;
    }

    private RefreshCondition doIn(PredicateAndOtherScript p, String property,List<? extends Object> list ){

        if (list == null || list.isEmpty())
            return this;

        List<Object> tempList = new ArrayList<Object>();
        for (Object obj : list) {
            if (Objects.isNull(obj))
                continue;

            if (!tempList.contains(obj)) {
                tempList.add(obj);
            }
        }

        if (tempList.isEmpty())
            return this;

        if (tempList.size() == 1) {
            return eq(property, tempList.get(0));
        }

        Criteria.X x = new Criteria.X();
        x.setConjunction(ConjunctionAndOtherScript.AND);
        x.setPredicate(p);
        x.setKey(property);
        x.setValue(tempList);

        this.listX.add(x);
        return this;
    }

    private RefreshCondition doNull(PredicateAndOtherScript p, String property){
        if (StringUtil.isNullOrEmpty(property))
            return this;

        Criteria.X x = new Criteria.X();
        x.setConjunction(ConjunctionAndOtherScript.AND);
        x.setPredicate(p);
        x.setValue(property);
        this.listX.add(x);
        return this;
    }
    
    public RefreshCondition eq(String property, Object value) {
        return this.doGle(PredicateAndOtherScript.EQ,property,value);
    }


    public RefreshCondition lt(String property, Object value) {
        return this.doGle(PredicateAndOtherScript.LT,property,value);
    }

  
    public RefreshCondition lte(String property, Object value) {
        return this.doGle(PredicateAndOtherScript.LTE,property,value);
    }


    public RefreshCondition gt(String property, Object value) {
        return this.doGle(PredicateAndOtherScript.GT,property,value);
    }


    public RefreshCondition gte(String property, Object value) {
        return this.doGle(PredicateAndOtherScript.GTE,property,value);
    }


    public RefreshCondition ne(String property, Object value) {
        return this.doGle(PredicateAndOtherScript.NE,property,value);
    }

 
    public RefreshCondition in(String property, List<?> list) {
        return this.doIn(PredicateAndOtherScript.IN,property,list);
    }

    public RefreshCondition nin(String property, List<Object> list) {
        return this.doIn(PredicateAndOtherScript.NOT_IN,property,list);
    }

  
    public RefreshCondition nonNull(String property) {
        return doNull(PredicateAndOtherScript.IS_NOT_NULL, property);
    }

    
    public RefreshCondition isNull(String property) {
        return doNull(PredicateAndOtherScript.IS_NULL, property);
    }

    
    public RefreshCondition x(String sql) {
        return x(sql, null);
    }

   
    public RefreshCondition x(String sql, List<Object> valueList) {
        if (StringUtil.isNullOrEmpty(sql))
            return this;

        sql = BeanUtilX.normalizeSql(sql);

        Criteria.X x = new Criteria.X();
        x.setConjunction(ConjunctionAndOtherScript.AND);
        x.setPredicate(PredicateAndOtherScript.X);
        x.setKey(sql);
        x.setValue(valueList);
        this.listX.add(x);

        return this;
    }

    public RefreshCondition routeKey(Object routeKey) {
        this.routeKey = routeKey;
        return this;
    }
}
