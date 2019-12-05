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
package x7.repository;

import x7.core.bean.Criteria;
import x7.core.bean.DomainObject;
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.RefreshCondition;
import x7.core.web.Page;

import java.util.List;
import java.util.Map;

/**
 *
 * x7-repository programmer API
 *
 * @author Sim
 *
 * @param <T>
 */
public interface BaseRepository<T> extends Typed<T>{

	long createId();
	void refreshCache();

	boolean createBatch(List<T> objList);

	long create(T obj);

	boolean refresh(RefreshCondition<T> refreshCondition);
	boolean refreshUnSafe(RefreshCondition<T> refreshCondition);

	boolean remove(String keyOne);
	boolean remove(long keyOne);
	/**
	 *
	 * @param keyOne
	 * 
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
	 * 
	 */
	List<T> list(T conditionObj);

	T getOne(T conditionObj);

	/**
	 * in API
	 * @param inCondition
	 * @return
	 */
	List<T> in(InCondition inCondition);

	/**
	 * Standard query pageable API
	 * 
	 * @param criteria
	 * 
	 */
	Page<T> find(Criteria criteria);

	/**
	 * Standard query pageable API, FETCH supported
	 * 
	 * @param criteria
	 */
	Page<Map<String, Object>> find(Criteria.ResultMappedCriteria criteria);


	List<Map<String,Object>> list(Criteria.ResultMappedCriteria resultMapped);

	List<T> list(Criteria criteria);

	<WITH> List<DomainObject<T,WITH>>  listDomainObject(Criteria.DomainObjectCriteria domainObjectCriteria);
}