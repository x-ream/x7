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
package io.xream.x7.repository;

import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.DomainObject;
import io.xream.x7.common.bean.condition.InCondition;
import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.common.web.Page;

import java.util.List;
import java.util.Map;

/**
 * jdbc-template-plus programmer API
 *
 * @param <T>
 * @author Sim
 */
public interface BaseRepository<T> extends Typed<T> {

    long createId();

    void refreshCache();

    boolean createBatch(List<T> objList);

    long create(T obj);

    boolean refresh(RefreshCondition<T> refreshCondition);

    boolean refreshUnSafe(RefreshCondition<T> refreshCondition);

    boolean remove(String keyOne);

    boolean remove(long keyOne);

    /**
     * @param keyOne
     */
    T get(long keyOne);

    T get(String keyOne);

    /**
     * LOAD
     *
     * @return
     */
    List<T> list();

    /**
     * 根据对象查询
     *
     * @param conditionObj
     */
    List<T> list(T conditionObj);

    /**
     * sometimes, we use the API, for example: <br>
     * 1. foo.setIsDeleted(false) and other condition  <br>
     * 2. get address  by various of condition <br>
     * 3. get user  by name <br>
     * <br>
     * Notes:  <br>
     * do not use getOne(obj) instead of get(keyOne), get(keyOne) is under findOne(ID) of JPA  <br>
     * anyway, getOne(obj) is like getOne(ID) of JPA <br>
     * is is not under findOne(ID)  <br>
     */
    T getOne(T conditionObj);

    /**
     * in API
     *
     * @param inCondition
     * @return
     */
    List<T> in(InCondition inCondition);

    /**
     * Standard query pageable API
     *
     * @param criteria
     */
    Page<T> find(Criteria criteria);

    /**
     * Standard query pageable API, FETCH supported
     *
     * @param criteria
     */
    Page<Map<String, Object>> find(Criteria.ResultMappedCriteria criteria);


    List<Map<String, Object>> list(Criteria.ResultMappedCriteria resultMapped);

    List<T> list(Criteria criteria);

    <WITH> List<DomainObject<T, WITH>> listDomainObject(Criteria.DomainObjectCriteria domainObjectCriteria);

}