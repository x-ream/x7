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
package x7.core.bean.condition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import x7.core.bean.Criteria;
import x7.core.bean.CriteriaBuilder;
import x7.core.bean.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RefreshCondition<T> {

    private Criteria condition;
    private List<Criteria.X> refreshList = new ArrayList<>();
    private String sourceStript;//FIXME fetch

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

    private CriteriaBuilder getBuilder(){
        if (this.builder == null) {
            CriteriaBuilder builder = CriteriaBuilder.buildCondition();
            this.builder = builder;
        }
        return this.builder;
    }


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
        x.setPredicate(Predicate.X);
        x.setKey(sqlX);
        this.refreshList.add(x);

        return this;
    }

    public RefreshCondition refresh(String property, Object value){

        if (Objects.isNull(value))
            return this;

        Criteria.X x = new Criteria.X();
        x.setPredicate(Predicate.EQ);
        x.setKey(property);
        x.setValue(value);
        this.refreshList.add(x);

        return this;
    }


}
