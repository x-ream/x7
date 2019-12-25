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
package x7.core.async;

/**
 * <li>心跳接口</li>
 * 必需异步实现tick方法，实现此接口的service，需要在构造或初始化时：<br>
 * HeartBeator.add(this);
 * @author wyan
 *
 */
public interface HeartBeat {
	/**
	 * 处理心跳循环的业务逻辑方法
	 * @param now
	 */
	void tick(long now);
	
}
