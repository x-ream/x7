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
package io.xream.x7.common.async;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 心跳循环器<br>
 * 实现了IHeartBeat接口的service需要注册到此心跳循环器
 * HeartBeator.add(this);
 * @author Sim
 * 
 */
public class HeartBeator {

	private final static long INTERVAL = 50;
	private final static TimeUnit UNIT = TimeUnit.MILLISECONDS;
	private final static ScheduledExecutorService service = Executors
			.newScheduledThreadPool(1);

	private final static CopyOnWriteArrayList<HeartBeat> tasks = new CopyOnWriteArrayList<HeartBeat>();

	private static HeartBeator instance;
	public static void newInstance(){
		if (instance == null){
			instance = new HeartBeator();
		}
	}
	
	private HeartBeator(){}
	
	static {
		schedule(new Runnable() {

			@Override
			public void run() {
				try {
					tick();
				} catch (NullPointerException npe){
					npe.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, System.currentTimeMillis() + 1000, INTERVAL);
	}

	private static void schedule(Runnable command, long scheduledAt, long delay) {
		long now = System.currentTimeMillis();
		if (scheduledAt <= now) {
			scheduledAt += 86400000L;
		}
		service.scheduleWithFixedDelay(command, scheduledAt - now, delay, UNIT);
	}

	private static void tick() {
		long now = System.currentTimeMillis();
		for (HeartBeat task : tasks){
			try{
				task.tick(now);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 注册心跳循环服务
	 * 
	 * @param task
	 */
	public static void add(HeartBeat task) {
		tasks.add(task);
	}

	/**
	 * 移除心跳循环服务
	 * 
	 * @param task
	 */
	public static void remove(HeartBeat task) {
		tasks.remove(task);
	}
}
