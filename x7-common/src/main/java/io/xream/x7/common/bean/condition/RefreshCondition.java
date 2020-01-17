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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RefreshCondition<T> implements Routeable {

    private Criteria condition;
    private List<Criteria.X> refreshList = new ArrayList<>();
    private String sourceStript;//FIXME

    private Object routeKey;
    @JsonIgnore
    private transient Class clz;
    @JsonIgnore
    private transient  CriteriaBuilder builder;

    public Class getClz() {
        return clz;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }

    public List<Criteria.X> getRefreshList() {
        return refreshList;
    }

    public void setRefreshList(List<Criteria.X> refreshList) {
        this.refreshList = refreshList;
    }

    public String getSourceStript() {
        return sourceStript;
    }

    public void setSourceStript(String sourceStript) {
        this.sourceStript = sourceStript;
    }
    @Override
    public Object getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(Object routeKey) {
        this.routeKey = routeKey;
    }
    public Criteria getCondition() {

        if (this.condition == null) {

            if (Objects.nonNull(this.builder)) {
                this.condition = builder.get();
            }
        }

        return this.condition;
    }

    public void setCondition(Criteria condition) {
        this.condition = condition;
    }

    public RefreshCondition(){
    }
//    private RefreshCondition(Class<T> clzz){
//        this.clz = clzz;
//    }
//
//    public static <T> RefreshCondition build(Class<T> clzz){
//        return new RefreshCondition(clzz);
//    }

    public static RefreshCondition build(){
        return new RefreshCondition();
    }

    private CriteriaBuilder getBuilder(){
        if (this.builder == null) {
            CriteriaBuilder builder = CriteriaBuilder.buildCondition();
            this.builder = builder;
        }
        return this.builder;
    }

    @Deprecated
    public CriteriaBuilder.ConditionBuilder  and(){

        return this.getBuilder().and();
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

    public KV getKeyOne() {
        Parsed parsed = Parser.get(clz);
        String keyOne = parsed.getKey(X.KEY_ONE);
        for (Criteria.X x : getCondition().getListX()) {
            String key = x.getKey();
            if (key != null && key.equals(keyOne)) {
                return new KV(key,x.getValue());
            }
        }
        return null;
    }

    
    public RefreshCondition eq(String property, Object value) {
        this.getBuilder().and().eq(property,value);
        return this;
    }


    public RefreshCondition lt(String property, Object value) {
        this.getBuilder().and().lt(property,value);
        return this;
    }

  
    public RefreshCondition lte(String property, Object value) {
        this.getBuilder().and().lte(property,value);
        return this;
    }


    public RefreshCondition gt(String property, Object value) {
        this.getBuilder().and().gt(property,value);
        return this;
    }


    public RefreshCondition gte(String property, Object value) {
        this.getBuilder().and().gte(property,value);
        return this;
    }


    public RefreshCondition ne(String property, Object value) {
        this.getBuilder().and().ne(property,value);
        return this;
    }

 
    public RefreshCondition like(String property, String value) {
        this.getBuilder().and().like(property,value);
        return this;
    }


    public RefreshCondition likeRight(String property, String value) {
        this.getBuilder().and().likeRight(property,value);
        return this;
    }

 
    public RefreshCondition notLike(String property, String value) {
        this.getBuilder().and().notLike(property,value);
        return this;
    }


    public RefreshCondition between(String property, Object min, Object max) {
        this.getBuilder().and().between(property,min,max);
        return this;
    }

 
    public RefreshCondition in(String property, List<?> list) {
        this.getBuilder().and().in(property,list);
        return this;
    }


    public RefreshCondition nin(String property, List<Object> list) {
        this.getBuilder().and().nin(property,list);
        return this;
    }

  
    public RefreshCondition nonNull(String property) {
        this.getBuilder().and().nonNull(property);
        return this;
    }

    
    public RefreshCondition isNull(String property) {
        this.getBuilder().and().isNull(property);
        return this;
    }

    
    public RefreshCondition x(String sql) {
        this.getBuilder().and().x(sql);
        return this;
    }

   
    public RefreshCondition x(String sql, List<Object> valueList) {
        this.getBuilder().and().x(sql,valueList);
        return this;
    }

    public RefreshCondition routeKey(Object routeKey) {
        this.routeKey = routeKey;
        return this;
    }
}
