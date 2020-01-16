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
package io.xream.x7.cache;

import io.xream.x7.common.cache.L2CacheResolver;
import io.xream.x7.common.util.JsonX;
import io.xream.x7.common.util.StringUtil;
import io.xream.x7.common.util.VerifyUtil;
import io.xream.x7.common.web.Page;
import io.xream.x7.exception.L2CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * 
 * Level Two Cache
 * @author sim
 *
 */
public class DefaultL2CacheResolver implements L2CacheResolver {

	private final static Logger logger = LoggerFactory.getLogger(DefaultL2CacheResolver.class);
	public final static String NANO_SECOND = ".ns.";


	private static int validSecond;
	private static boolean isEnabled;
	public static void enabled(){
		isEnabled = true;
	}

	public boolean isEnabled(){
		return isEnabled;
	}
	public static void setValidSecond(int vs){
		validSecond = vs;
		System.out.println("\n");
		logger.info("L2 Cache try to starting.... cache time = " + validSecond + "s");
		System.out.println("\n");
	}
	private int getValidSecondAdjusted(){
		return  this.validSecond;
	}

	private L2CacheStorage cacheStorage;
	public void setCachestorage(L2CacheStorage cacheStorage){
		this.cacheStorage = cacheStorage;
	}

	protected L2CacheStorage getCachestorage(){
		if (this.cacheStorage == null)
			throw new RuntimeException("No implements of L2CacheStorage, like the project jdbc-template-plus/redis-integration");
		return this.cacheStorage;
	}
	/**
	 * 标记缓存要更新
	 * @param clz
	 * @return nanuTime_String
	 */
	@SuppressWarnings("rawtypes")
	public String markForRefresh(Class clz){

		String key = getNSKey(clz);
		String time = String.valueOf(System.nanoTime());
		boolean flag = getCachestorage().set(key, time);
		if (!flag)
			throw new L2CacheException("markForRefresh failed");
		return time;
	}
	
	/**
	 * 
	 * FIXME {hash tag}
	 */
	@SuppressWarnings("rawtypes")
	public void remove(Class clz, String key){
		key = getSimpleKey(clz, key);
		boolean flag = getCachestorage().delete(key);
		if (!flag)
			throw new L2CacheException("remove failed");
	}

	public void remove(Class clz) {

		String key = getSimpleKeyLike(clz);

		Set<String> keySet = getCachestorage().keys(key);

		for (String k : keySet) {
			boolean flag = getCachestorage().delete(k);
			if (!flag)
				throw new L2CacheException("remove failed");
		}

	}
	
	@SuppressWarnings("rawtypes")
	private String getNSKey(Class clz){
		return clz.getName()+ NANO_SECOND;
	}
	
	@SuppressWarnings("unused")
	private String getNS(String nsKey){
		return getCachestorage().get(nsKey);
	}
	
	@SuppressWarnings("rawtypes")
	private List<String> getKeyList(Class clz, List<String> conditionList){
		if (conditionList == null || conditionList.isEmpty())
			return null;
		List<String> keyList = new ArrayList<>();
		for (String condition : conditionList){
			String key = getSimpleKey(clz, condition);
			keyList.add(key);
		}
		if (keyList.isEmpty())
			return null;

		return keyList;
	}
	
	/**
	 * FIXME 有简单simpleKey的地方全改成字符串存储, value为bytes, new String(bytes)
	 * @param clz
	 * @param condition
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String getSimpleKey(Class clz, String condition){
		return "{"+clz.getName()+"}." + condition;
	}

	private String getTotalRowsKey(Class clz, String condition){
		condition = VerifyUtil.toMD5(condition) + "~TR";
		return getSimpleKey(clz, condition);
	}

	private String getSimpleKeyLike(Class clz){
		return "{"+clz.getName()+"}.*" ;
	}
	
	
	@SuppressWarnings("rawtypes")
	private String getKey(Class clz, Object conditionObj){
		String condition = JsonX.toJson(conditionObj);
		return  getPrefix(clz) +"."+VerifyUtil.toMD5(condition);
	}

	
	/**
	 * 获取缓存KEY前缀
	 * @param clz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String getPrefix(Class clz){
		String key = getNSKey(clz);
		String nsStr = getCachestorage().get(key);
		if (nsStr == null){
			String str = markForRefresh(clz);
			return "{"+clz.getName()+"}." + str;
		}
		return "{"+clz.getName()+"}."  + nsStr;
	}

	/**
	 * FIXME {hash tag}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void set(Class clz, String key, Object obj) {
		key = getSimpleKey(clz, key);
		int validSecond =  getValidSecondAdjusted();
		getCachestorage().set(key, JsonX.toJson(obj), validSecond,TimeUnit.SECONDS);
	}

	@Override
	public void setTotalRows(Class clz, String key, long obj) {
		key = getTotalRowsKey(clz, key);
		int validSecond =  getValidSecondAdjusted();
		getCachestorage().set(key, String.valueOf(obj), validSecond,TimeUnit.SECONDS);
	}


	@SuppressWarnings("rawtypes")
	@Override
	public void setResultKeyList(Class clz, Object condition, List<String> keyList) {
		String key = getKey(clz, condition);
		try{
			int validSecond = getValidSecondAdjusted();
			getCachestorage().set(key, JsonX.toJson(keyList), validSecond,TimeUnit.SECONDS);
		}catch (Exception e) {
			throw new L2CacheException(e.getMessage());
		}
	}

	
	@Override
	public <T> void setResultKeyListPaginated(Class<T> clz, Object condition, Page<T> pagination) {
		String key = getKey(clz, condition);
		try{
			int validSecond = getValidSecondAdjusted();
			getCachestorage().set(key, JsonX.toJson(pagination), validSecond, TimeUnit.SECONDS);
		}catch (Exception e) {
			throw new L2CacheException(e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> getResultKeyList(Class clz, Object condition) {
		String key = getKey(clz, condition);
		String str = getCachestorage().get(key);
		if (StringUtil.isNullOrEmpty(str))
			return new ArrayList<String>();
		
		return JsonX.toList(str, String.class);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Page<String> getResultKeyListPaginated(Class clz, Object condition) {
		String key = getKey(clz, condition);
		String json = getCachestorage().get(key);
		
		if (StringUtil.isNullOrEmpty(json))
			return null;
		
		return ObjectUtil.toPagination(json);
	}

	@Override
	public <T> List<T> list(Class<T> clz, List<String> keyList) {
		List<String> keyArr = getKeyList(clz, keyList);//转换成缓存需要的keyList
		
		List<String> jsonList = getCachestorage().multiGet(keyArr);
		
		if (jsonList == null)
			return new ArrayList<T>();
		
		List<T> list = new ArrayList<T>();
		for (String json : jsonList){
			if (StringUtil.isNotNull(json)) {
				T t = JsonX.toObject(json,clz);
				list.add(t);
			}
		}
		
		return list;
	}

	/**
	 * FIXME {hash tag}
	 */
	@Override
	public <T> T get(Class<T> clz, String key) {
		key = getSimpleKey(clz,key);
		String str = getCachestorage().get(key);
		if (StringUtil.isNullOrEmpty(str))
			return null;
		T obj = JsonX.toObject(str,clz);
		return obj;
	}

	@Override
	public <T> long getTotalRows(Class<T> clz, String key) {
		key = getTotalRowsKey(clz,key);
		String str = getCachestorage().get(key);
		if (StringUtil.isNullOrEmpty(str))
			return 0;
		return Long.valueOf(str);
	}

	@Override
	public void setMapList(Class clz, String key, List<Map<String, Object>> mapList) {
		key = getSimpleKey(clz, key);
		int validSecond =  getValidSecondAdjusted();

		getCachestorage().set(key, JsonX.toJson(mapList), validSecond,TimeUnit.SECONDS);
	}

	@Override
	public List<Map<String, Object>> getMapList(Class clz, String key) {
		key = getSimpleKey(clz,key);
		String str = getCachestorage().get(key);
		if (StringUtil.isNullOrEmpty(str))
			return null;
		List mapList = JsonX.toList(str,Map.class);
		return mapList;
	}


}
