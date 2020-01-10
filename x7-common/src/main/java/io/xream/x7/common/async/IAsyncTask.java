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

/**
 * 
 * 任何复杂的计算，但计算结果不影响后面的计算的情况下，可以创建临时任务<br>
 * CasualWorker.accept(new ICasualTask(){execute(){}});<br>
 * 适应举例：<br>
 * 1. 在创建场景时，初始化场景的数据<br>
 * 2. 在倒计时的时间段里，<br>
 * 
 * 直接在使用时创建匿名实例<br>
 * 
 * @author Sim
 *
 */
public interface IAsyncTask{
	void execute() throws Exception;
}
