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
package x7.repository.dao;

import x7.core.bean.Criteria;
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.RefreshCondition;
import x7.core.web.Page;
import x7.repository.KeyOne;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author Sim
 *
 */
public interface Dao {

	long create(Object obj);

	boolean createBatch(List<? extends Object> objList);

	<T> boolean remove(KeyOne<T> keyOne);

	<T> boolean refreshByCondition(RefreshCondition<T> conditon);
	
	<T> List<T> list(Object conditionObj);
	
	List<Map<String,Object>>  list(Class clz, String sql,
			List<Object> conditionList);

	<T> T getOne(T conditionObj);
	
	<T> List<T> in(InCondition inCondition);
	
	Page<Map<String, Object>> find(Criteria.ResultMappedCriteria resultMapped);

	List<Map<String,Object>> list(Criteria.ResultMappedCriteria resultMapped);

	<T> Page<T> find(Criteria criteria);

	<T> List<T> list(Criteria criteria);

	@Deprecated
	<T>boolean execute(T obj, String sql);
}