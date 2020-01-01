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

public interface AsyncDao {

	/**
	 * 持久化对象<br>
	 * 限定：同样的对象不能在同一时间段被创建2次，如果需要多条记录，请创建多个实例<br>
	 * @param obj
	 * @throws Exception
	 */
	void create(Object obj);

	/**
	 * 更新对象<br>
	 * 一般要查出对象在更新
	 * 
	 * @param obj
	 * 
	 */
	void refresh(Object obj);

	/**
	 * 更新对象<br>
	 * 
	 * @param obj
	 * @throws Exception
	 */
	void remove(Object obj);

}