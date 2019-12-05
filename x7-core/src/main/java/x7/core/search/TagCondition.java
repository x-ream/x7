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
package x7.core.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TagCondition implements Serializable{

	private static final long serialVersionUID = 8909511910631113572L;
	private Map<String, Long> map = new HashMap<String,Long>();
	public Map<String, Long> getMap() {
		return map;
	}
	public void setMap(Map<String, Long> map) {
		this.map = map;
	}
	
	public Long getId(String tagKey){
		return this.map.get(tagKey);
	}
	@Override
	public String toString() {
		return "TagCondition [map=" + map + "]";
	}
}
