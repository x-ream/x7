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
package x7.core.repository;

import x7.core.util.JsonX;
import x7.core.web.Page;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 
 * 缓存<br>
 * @author sim
 *
 */
public interface CacheResolver {

	boolean isEnabled();
	/**
	 * 标记缓存要更新
	 * @param clz
	 * @return nanuTime_String
	 */
	@SuppressWarnings("rawtypes")
	String markForRefresh(Class clz);
	
	void remove(Class clz, String key);

	void remove(Class clz);

	
	/**
	 * 高效的缓存单个对象<br>
	 * @param clz
	 * @param key
	 * @param obj
	 */
	@SuppressWarnings("rawtypes")
	void set(Class clz, String key, Object obj);
	
	/**
	 * 获取高效的缓存单个对象<br>
	 * @param clz
	 * @param key
	 */
	<T> T get(Class<T> clz, String key);
	/**
	 * 高效的缓存Key列表
	 * @param clz
	 * @param conditionObj
	 * @param keyList
	 */
	@SuppressWarnings("rawtypes")
	void setResultKeyList(Class clz, Object conditionObj, List<String> keyList);
	
	/**
	 * 
	 * @param clz
	 * @param conditionObj
	 * @param pagination
	 */
	<T> void setResultKeyListPaginated(Class<T> clz, Object conditionObj, Page<T> pagination);


	
	/**
	 * 高效的获取缓存对象Key列表<br>
	 * 如果没有值则返回null, 表示需要更新缓存<br>
	 * 返回的列表里没有元素, 为正常缓存数据<br>
	 * @param clz
	 * @param conditionObj
	 * @return obj
	 */
	@SuppressWarnings("rawtypes")
	List<String> getResultKeyList(Class clz, Object conditionObj);
	/**
	 * 高效的获取缓存分页列表<br>
	 * 如果没有值则返回null, 表示需要更新缓存<br>
	 * 返回的列表里没有元素, 为正常缓存数据<br>
	 * @param clz
	 * @param conditionObj
	 * @return obj
	 */
	<T> Page<T> getResultKeyListPaginated(Class<T> clz, Object conditionObj);
	/**
	 * 高效从缓存中查出符合条件的所以对象
	 * @param clz
	 * @param keyList 
	 */
	@SuppressWarnings("rawtypes")
	<T> List<T> list(Class<T> clz, List<String> keyList);
	
	void setMapList(Class clz, String key, List<Map<String,Object>> mapList);
	
	List<Map<String,Object>> getMapList(Class clz, String key);

	default String createCondition(Object obj){
		if (Objects.isNull(obj))
			return "null";
		return JsonX.toJson(obj);
	}
}
