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

import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.Parsed;
import io.xream.x7.common.bean.Parser;
import io.xream.x7.common.bean.condition.InCondition;
import io.xream.x7.common.cache.L2CacheConsistency;
import io.xream.x7.common.cache.L2CacheResolver;
import io.xream.x7.common.cache.L2CacheStorage;
import io.xream.x7.common.repository.X;
import io.xream.x7.common.util.*;
import io.xream.x7.common.web.Page;
import io.xream.x7.exception.L2CacheException;
import io.xream.x7.exception.NoResultUnderProtectionException;
import io.xream.x7.exception.NotQueryUnderProtectionException;
import io.xream.x7.repository.QueryForCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


/**
 * 
 * Level Two Cache
 * @author sim
 *
 */
public final class DefaultL2CacheResolver implements L2CacheResolver {

	private final static Logger logger = LoggerFactory.getLogger(DefaultL2CacheResolver.class);
	public final static String NANO_SECOND = ".ns.";

	private static int validSecond;
	private static boolean isEnabled;

    private L2CacheConsistency l2CacheConsistency;
    @Override
    public void setL2CacheConsistency(L2CacheConsistency l2CacheConsistency){
        this.l2CacheConsistency = l2CacheConsistency;
    }

	public static void enabled(){
		isEnabled = true;
	}

	public boolean isEnabled(){
		return isEnabled;
	}
	public static void setValidSecond(int vs){
		validSecond = vs;
		logger.info("L2 Cache try to starting.... cache time = {}s",validSecond);
	}
	private int getValidSecondAdjusted(){
		return  this.validSecond;
	}

	private L2CacheStorage cacheStorage;
	public void setCacheStorage(L2CacheStorage cacheStorage){
		this.cacheStorage = cacheStorage;
	}

	protected L2CacheStorage getCachestorage(){
		if (this.cacheStorage == null)
			throw new RuntimeException("No implements of L2CacheStorage, like the project x7-repo/x7-redis-integration");
		return this.cacheStorage;
	}
	/**
	 * 标记缓存要更新
	 * @param clz
	 * @return nanuTime_String
	 */
	@SuppressWarnings("rawtypes")
	public String markForRefresh(Class clz){

        if (this.l2CacheConsistency != null){
            this.l2CacheConsistency.markForRefresh(clz);
        }

		String key = getNSKey(clz);
		String time = String.valueOf(System.nanoTime());
		getCachestorage().set(key, time);

		return time;
	}

	@Override
	public boolean refresh(Class clz, String key) {
		if (key == null){
			remove(clz);
		}else{
			remove(clz, key);
		}
		markForRefresh(clz);
		return true;
	}

	@Override
	public boolean refresh(Class clz) {
		return refresh(clz, null);
	}

	/**
	 * 
	 * FIXME {hash tag}
	 */
	@SuppressWarnings("rawtypes")
	public void remove(Class clz, String key){

        if (this.l2CacheConsistency != null){
            this.l2CacheConsistency.remove(clz,key);
        }

		key = getSimpleKey(clz, key);
		getCachestorage().delete(key);
	}

	public void remove(Class clz) {

        if (this.l2CacheConsistency != null){
            this.l2CacheConsistency.remove(clz);
        }

		String key = getSimpleKeyLike(clz);

		Set<String> keySet = getCachestorage().keys(key);

		for (String k : keySet) {
			getCachestorage().delete(k);
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
	private List<String> getKeyList(Class clz, List<String> conditionSet){
		if (conditionSet == null || conditionSet.isEmpty())
			return null;
		List<String> keyList = new ArrayList<>();
		for (String condition : conditionSet){
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
		String condition;
		if (conditionObj == null){
			condition = DEFAULT_VALUE;
		}else {
			condition = JsonX.toJson(conditionObj);
		}
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



	private void setTotalRows(Class clz, String key, long obj) {
		key = getTotalRowsKey(clz, key);
		int validSecond =  getValidSecondAdjusted();
		getCachestorage().set(key, String.valueOf(obj), validSecond,TimeUnit.SECONDS);
	}



	private void setResultKeyList(Class clz, Object condition, List<String> keyList) {
		String key = getKey(clz, condition);
		try{
			int validSecond = getValidSecondAdjusted();
			getCachestorage().set(key, JsonX.toJson(keyList), validSecond,TimeUnit.SECONDS);
		}catch (Exception e) {
			throw new L2CacheException(e.getMessage());
		}
	}

	
	private  <T> void setResultKeyListPaginated(Class<T> clz, Object condition, Page<T> pagination) {
		String key = getKey(clz, condition);
		try{
			int validSecond = getValidSecondAdjusted();
			getCachestorage().set(key, JsonX.toJson(pagination), validSecond, TimeUnit.SECONDS);
		}catch (Exception e) {
			throw new L2CacheException(e.getMessage());
		}
	}

	private List<String> getResultKeyList(Class clz, Object condition) throws NotQueryUnderProtectionException{
		String key = getKey(clz, condition);
		String str = getCachestorage().get(key);
		if (StringUtil.isNullOrEmpty(str))
			throw new NotQueryUnderProtectionException();
		
		return JsonX.toList(str, String.class);
	}
	
	private Page<String> getResultKeyListPaginated(Class clz, Object condition) {
		String key = getKey(clz, condition);
		String json = getCachestorage().get(key);
		
		if (StringUtil.isNullOrEmpty(json))
			return null;
		
		return ObjectUtil.toPagination(json);
	}

	private  <T> List<T> list(Class<T> clz, List<String> keyList) {
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
	private void set(Class clz, String key, Object obj) {
		key = getSimpleKey(clz, key);
		doSet(key,obj);
	}

	/**
	 * FIXME {hash tag}
	 */
	private void set(Class clz, Object objKey, Object obj) {
		String key = getKey(clz, objKey);
		doSet(key,obj);
	}

	private void doSet(String key, Object obj) {

		int validSecond =  getValidSecondAdjusted();
		String value;
		if (obj == null){
			value = DEFAULT_VALUE;
		}else {
			value = JsonX.toJson(obj);
		}
		getCachestorage().set(key, value, validSecond,TimeUnit.SECONDS);
	}

	/**
	 * FIXME {hash tag}
	 */
	private  <T> T get(Class<T> clz, String key) throws NoResultUnderProtectionException{
		key = getSimpleKey(clz,key);
		return doGet(clz,key);
	}

	private  <T> T get(Class<T> clz, Object objKey) throws NoResultUnderProtectionException{
		String key = getKey(clz,objKey);
		return doGet(clz,key);
	}

	private <T> T doGet(Class<T> clz, String key) throws NoResultUnderProtectionException{
		String str = getCachestorage().get(key);
		if (StringUtil.isNullOrEmpty(str))
			return null;
		if (str.trim().equals(DEFAULT_VALUE))
			throw new NoResultUnderProtectionException();
		T obj = JsonX.toObject(str,clz);
		return obj;
	}

	private  <T> long getTotalRows(Class<T> clz, String key) {
		key = getTotalRowsKey(clz,key);
		String str = getCachestorage().get(key);
		if (StringUtil.isNullOrEmpty(str))
			return DEFAULT_NUM;
		return Long.valueOf(str);
	}


	@Override
	public <T> List<T> listUnderProtection(Class<T> clz, Object conditionObj, QueryForCache queryForCache, Callable<List<T>> callable) {

		Parsed parsed = Parser.get(clz);
		List<String> keyList = null;
		try {
			keyList = getResultKeyList(clz, conditionObj);
		}catch (NotQueryUnderProtectionException upe) {

		}
		if (keyList == null) {

			List<T> list = null;
			try {
				list = callable.call();
			} catch (Exception e) {
				throw new RuntimeException(ExceptionUtil.getMessage(e));
			}

			keyList = new ArrayList<String>();

			for (T t : list) {
				String key = BeanUtilX.getCacheKey(t, parsed);
				keyList.add(key);
			}

			setResultKeyList(clz, conditionObj, keyList);

			return list;
		}

		if (keyList.isEmpty())
			return new ArrayList<>();

		List<T> list = list(clz, keyList);

		if (keyList.size() == list.size())
			return list;

		replenishAndRefreshCache(keyList, list, clz, parsed,queryForCache);

		List<T> sortedList = sort(keyList, list, parsed);

		return sortedList;
	}

	@Override
	public <T> T getUnderProtection(Class<T> clz, Object conditionObj, Callable<T> callable) {

		T obj;
		try{
			obj = get(clz,conditionObj);
		}catch (NoResultUnderProtectionException e){
			return null;
		}

		if (obj == null) {
			try {
				obj = callable.call();
			}catch (Exception e){
				throw new RuntimeException(ExceptionUtil.getMessage(e));
			}
			set(clz, conditionObj, obj);
		}

		return obj;
	}

	@Override
	public <T> Page<T> findUnderProtection(Criteria criteria,QueryForCache queryForCache, Callable<Page<T>> findCallable, Callable<List<T>> listCallable){
		Class clz = criteria.getClz();
		Parsed parsed = Parser.get(clz);
		Page p = getResultKeyListPaginated(clz, criteria);// FIXME

		if (p == null) {

			final String totalRowsString = getTotalRowsString(criteria);

			if (!criteria.isTotalRowsIgnored()) {
				// totalRows from cache
				long totalRows = getTotalRows(clz, totalRowsString);
				if (totalRows == DEFAULT_NUM) {
					try {
						p = findCallable.call();
					}catch (Exception e){
						throw new RuntimeException(ExceptionUtil.getMessage(e));
					}

					setTotalRows(clz, totalRowsString, p.getTotalRows());

				} else {
					List<T> list = null;
					try {
						list = listCallable.call();
					} catch (Exception e) {
						throw new RuntimeException(ExceptionUtil.getMessage(e));
					}
					p = new Page<>();
					p.setTotalRows(totalRows);
					p.setPage(criteria.getPage());
					p.setRows(criteria.getRows());
					p.reSetList(list);
				}
			} else {
				try {
					p = findCallable.call();
				}catch (Exception e){
					throw new RuntimeException(ExceptionUtil.getMessage(e));
				}
			}

			List<T> list = p.getList(); // 结果

			List<String> keyList = p.getKeyList();

			for (T t : list) {
				String key = BeanUtilX.getCacheKey(t, parsed);
				keyList.add(key);
			}

			p.reSetList(null);

			setResultKeyListPaginated(clz, criteria, p);

			p.setKeyList(null);
			p.reSetList(list);

			return p;
		}

		List<String> keyList = p.getKeyList();

		if (keyList == null || keyList.isEmpty()) {
			return p;
		}

		List<T> list = list(clz, keyList);

		if (keyList.size() == list.size()) {
			p.reSetList(list);
			return p;
		}

		replenishAndRefreshCache(keyList, list, clz, parsed, queryForCache);

		List<T> sortedList = sort(keyList, list, parsed);

		p.reSetList(sortedList);

		return p;
	}

	private  <T> void replenishAndRefreshCache(List<String> keyList, List<T> list, Class<T> clz, Parsed parsed, QueryForCache queryForCache) {

		Set<String> keySet = new HashSet<String>();
		for (T t : list) {
			String key = BeanUtilX.getCacheKey(t, parsed);
			keySet.add(key);
		}

		Field f = parsed.getKeyField(X.KEY_ONE);
		Class keyClz = f.getType();
		List<Object> idList = new ArrayList<>();
		for (String key : keyList) {
			if (!keySet.contains(key)) {
				try {
					if (keyClz == String.class) {
						idList.add(key);
					} else if (keyClz == long.class || keyClz == Long.class) {
						idList.add(Long.valueOf(key));
					} else if (keyClz == int.class || keyClz == Integer.class) {
						idList.add(Integer.valueOf(key));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		String key = parsed.getKey(X.KEY_ONE);
		InCondition inCondition = InCondition.wrap(key, idList);
		inCondition.setClz(clz);
		List<T> objList = queryForCache.in(inCondition);

		if (objList.isEmpty()) {
			markForRefresh(clz);
			return;
		}

		try {
			for (T obj : objList) {
				list.add(obj);
				Object id = f.get(obj);
				set(clz, String.valueOf(id), obj);
			}
		} catch (Exception e) {

		}

	}

	private  <T> List<T> sort(List<String> keyList, List<T> list, Parsed parsed) {
		List<T> sortedList = new ArrayList<T>();
		for (String key : keyList) {
			Iterator<T> ite = list.iterator();
			while (ite.hasNext()) {
				T t = ite.next();
				if (key.equals(BeanUtilX.getCacheKey(t, parsed))) {
					ite.remove();
					sortedList.add(t);
					break;
				}
			}
		}
		return sortedList;
	}

	private String getTotalRowsString(Criteria criteria) {
		int page = criteria.getPage();
		criteria.setPage(0);
		String str = JsonX.toJson(criteria);
		criteria.setPage(page);
		return str;
	}

}
