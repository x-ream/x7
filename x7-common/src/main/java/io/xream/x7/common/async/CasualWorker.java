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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 
 * @author Sim
 *
 */
public final class CasualWorker {
	
	private final static ExecutorService service = Executors.newFixedThreadPool(1);
	
	private final static BlockingQueue<Runnable> tasks = new ArrayBlockingQueue<>(4096);
	
	static {
		
		service.execute(() -> {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (true) {
				try {
					tasks.take().run();
				} catch (NullPointerException npe){
					npe.printStackTrace();
				}catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});

	}
	/**
	 * 接受临时任务，异步执行
	 * @param task
	 * @throws InterruptedException
	 */
	public static void accept(Runnable task) {
		try {
			tasks.put(task);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
