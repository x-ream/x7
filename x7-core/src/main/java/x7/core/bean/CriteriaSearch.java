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
package x7.core.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriteriaSearch implements Serializable{
	
	private static final long serialVersionUID = -2112313211719382190L;

	private List<Range> list = new ArrayList<Range>();
	private Map<String, Object> termMap = new HashMap<String, Object>();
	private Map<String, Object> termNotMap = new HashMap<String, Object>();
	
	public List<Range> getList() {
		return list;
	}
	
	public void add(Range range) {
		list.add(range);
	}

	public Map<String, Object> getTermMap() {
		return termMap;
	}

	public void setTermMap(Map<String, Object> termMap) {
		this.termMap = termMap;
	}
	
	/**
	 * value 暂时不支持中文 <br>
	 * @param key
	 * @param value
	 */
	public void putTerm(String key, Object value) {
		this.termMap.put(key, value);
	}

	public Map<String, Object> getTermNotMap() {
		return termNotMap;
	}

	public void setTermNotMap(Map<String, Object> termNotMap) {
		this.termNotMap = termNotMap;
	}
	
	public void putTermNot(String key, Object value) {
		this.termNotMap.put(key, value);
	}



	public enum Key{
		bool,
		must,
		range,
		term
	}
	
	public class Range implements Serializable{
		
		private static final long serialVersionUID = 3441017162584728968L;
		public Range(String key, Long from, Long to) {
			
			if (from != null && to != null && to < from)
				throw new RuntimeException("CriteriaSearch.Range: to must gte from");
			this.key = key;
			this.from = from;
			this.to = to;
		}
		private String key;
		private Long from;
		private Long to;
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public Long getFrom() {
			return from;
		}
		public void setFrom(Long from) {
			this.from = from;
		}
		public Long getTo() {
			return to;
		}
		public void setTo(Long to) {
			this.to = to;
		}
		@Override
		public String toString() {
			return "Range [key=" + key + ", from=" + from + ", to=" + to + "]";
		}
	}
	
}
