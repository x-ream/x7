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
package io.xream.x7.repository.dao;

import io.xream.x7.common.async.HeartBeat;
import io.xream.x7.common.async.HeartBeator;
import io.xream.x7.common.bean.BeanElement;
import io.xream.x7.common.bean.Parsed;
import io.xream.x7.common.bean.Parser;
import io.xream.x7.common.repository.X;
import io.xream.x7.common.util.TimeUtil;
import io.xream.x7.repository.KeyOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



@Component
public class AsyncDaoImpl implements HeartBeat, AsyncDao {

	/**
	 * 延时1分钟
	 */
	private final static int HEARTBEAT_DELAY = (int) TimeUtil.ONE_MINUTE;
	private long heartBeatTime = 0; // 降低心跳的实时性来提高性能

	private final ExecutorService mainExecutor = Executors.newSingleThreadExecutor();

	private final ExecutorService inner = Executors.newSingleThreadExecutor();


	public AsyncDaoImpl() {
		HeartBeator.add(this);
	}



	@SuppressWarnings("rawtypes")
	private  Map<Class, ArrayList<Object>> creationMap = new HashMap<Class, ArrayList<Object>>();

	@SuppressWarnings("rawtypes")
	private  Map<Class, ArrayList<Object>> refreshMap = new HashMap<Class, ArrayList<Object>>();
	
	@SuppressWarnings("rawtypes")
	private  Map<Class, ArrayList<Object>> removeMap = new HashMap<Class, ArrayList<Object>>();


	@Autowired
	private Dao dao;

	/*
	 *
	 */
	@Override
	public void create(final Object obj) {
		mainExecutor.submit(new Runnable() {

			@SuppressWarnings("rawtypes")
			@Override
			public void run()  {

				/*
				 * 加入需要持久化的的对象MAP
				 */
				Class clz = obj.getClass();
				
				ArrayList<Object> objectList = creationMap.get(clz);
				if (objectList == null) {
					objectList = new ArrayList<>();
					creationMap.put(clz, objectList);
				}
				/*
				 * 如果不存在，就添加
				 */
				if (!objectList.contains(obj)) {
					objectList.add(obj);
				}

			}
		});

	}
	
	/*
	 *
	 */
	@Override
	public void refresh(final Object obj) {
		mainExecutor.submit(() -> {
			/*
			 * 加入需要持久化的的对象MAP
			 */
			Class clz = obj.getClass();

			ArrayList<Object> objectList = refreshMap.get(clz);
			if (objectList == null) {
				objectList = new ArrayList<>();
				refreshMap.put(clz, objectList);
			}
			/*
			 * 如果不存在，就添加
			 */
			if (!objectList.contains(obj)) {
				objectList.add(obj);
			}

		});

	}

	/*
	 *
	 */
	@Override
	public void remove(final Object obj) {
		mainExecutor.submit(() -> {

			/*
			 * 加入需要持久化的的对象MAP
			 */
			Class clz = obj.getClass();

			ArrayList<Object> objectList = removeMap.get(clz);
			if (objectList == null) {
				objectList = new ArrayList<>();
				removeMap.put(clz, objectList);
			}
			/*
			 * 如果不存在，就添加
			 */
			if (!objectList.contains(obj)) {
				objectList.add(obj);
			}

		});

	}
	/**
	 * 批处理
	 * 
	 * @throws Exception
	 */
	private void batch() throws Exception {

		/*
		 * 创建批处理
		 */
		stepCreate();

		/*
		 * 更新批处理
		 */
		stepRefresh();
		
		/*
		 * 删除处理
		 */
		stepRemove();

	}

	@SuppressWarnings("rawtypes")
	private void stepCreate() {
		if (creationMap.size() == 0)
			return;
		/*
		 * 执行前的拷贝
		 */
		final Map<Class, ArrayList<Object>> tempCreationMap = new HashMap<Class, ArrayList<Object>>();
		tempCreationMap.putAll(creationMap);
		creationMap.clear();

		/*
		 * 切换内部线程处理
		 */
		inner.execute(() -> {
			try {
				/*
				 * 执行批处理
				 */
				executeCreate(tempCreationMap);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

	}

	/**
	 * 批处理的执行<br>
	 * 在内部线程里调用
	 */
	@SuppressWarnings({ "rawtypes" })
	private void executeCreate(final Map<Class, ArrayList<Object>> tempMap) {
		for (Class clz : tempMap.keySet()) {

			ArrayList<Object> objList = tempMap.get(clz);
			this.dao.createBatch(objList);

		}
	}

	
	@SuppressWarnings("rawtypes")
	private void stepRemove()  {
		if (removeMap.size() == 0)
			return;
		/*
		 * 执行前的拷贝
		 */
		final Map<Class, ArrayList<Object>> tempRefreshMap = new HashMap<Class, ArrayList<Object>>();
		tempRefreshMap.putAll(removeMap);
		removeMap.clear();

		/*
		 * 切换内部线程处理
		 */
		inner.execute(() -> {
			try {
				/*
				 * 执行批处理
				 */
				executeRemove(tempRefreshMap);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

	}

	/**
	 * 批处理的执行<br>
	 * 在内部线程里调用
	 */
	@SuppressWarnings("rawtypes")
	private void executeRemove(final Map<Class, ArrayList<Object>> tempMap) {
		for (ArrayList<Object> obj : tempMap.values()) {
			Class clz = obj.getClass();
			Parsed parsed = Parser.get(clz);
			String key = parsed.getKey(X.KEY_ONE);

			BeanElement be = parsed.getElement(key);

			try {
				Object keyOne = be.getMethod.invoke(obj, key);

				this.dao.remove(new KeyOne<Object>() {

					@Override
					public Object get() {
						return keyOne;
					}

					@Override
					public Class<Object> getClzz() {
						return clz;
					}
				});
			}catch (Exception e){

			}
		}
	}

	
	
	@SuppressWarnings("rawtypes")
	private void stepRefresh() {
		if (refreshMap.size() == 0)
			return;
		/*
		 * 执行前的拷贝
		 */
		final Map<Class, ArrayList<Object>> tempRefreshMap = new HashMap<Class, ArrayList<Object>>();
		tempRefreshMap.putAll(refreshMap);
		refreshMap.clear();

		/*
		 * 切换内部线程处理
		 */
		inner.execute(() -> {
			try {
				/*
				 * 执行批处理
				 */
				executeRefresh(tempRefreshMap);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

	}
	

	/**
	 * 批处理的执行<br>
	 * 在内部线程里调用
	 */
	@SuppressWarnings({ "rawtypes"})
	private void executeRefresh(final Map<Class, ArrayList<Object>> tempMap) {
		//TODO:
	}

	
	/**
	 * 内部机制, 永远不能调用此方法
	 */
	@Override
	@Deprecated
	public void tick(long now) {
		onHeartBeat(now);

	}

	/**
	 * 定时处理
	 */
	private void onHeartBeat(final long now) {
		mainExecutor.submit(() -> {
			if (heartBeatTime == 0){
				heartBeatTime = now;
				return;
			}
			if (now - heartBeatTime >= HEARTBEAT_DELAY) {
				heartBeatTime = now;
				/*
				 * 定时批处理
				 */
				try {
					batch();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * <br>
	 * 关机时, 或业务上要求立即执行时 调用<br>
	 * 不必等待定时处理,调用后, DAO线程将立即处理<br>
	 * 在没有了在线玩家的情况下,可以保证数据的完整性<br>
	 * 设计上, 没有在shutDownHook的线程里运行, 如何保证执行完毕 ? <br>
	 * 建议在shutDownHook的最后一步调用Thread.sleep(?) <br>
	 * <br>
	 */
	public void doImmediately() {
		mainExecutor.submit(() -> {
			try {
				batch();
			}catch (Exception e){
				e.printStackTrace();
			}
		});
	}

}
