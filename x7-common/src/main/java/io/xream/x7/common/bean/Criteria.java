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
package io.xream.x7.common.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.xream.x7.common.util.BeanUtil;
import io.xream.x7.common.util.BeanUtilX;
import io.xream.x7.common.util.StringUtil;
import io.xream.x7.common.web.Paged;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author sim
 *
 */
public class Criteria implements CriteriaCondition, Paged, Routeable,Serializable {

	private static final long serialVersionUID = 7088698915888081349L;

	private Class<?> clz;
	private boolean isTotalRowsIgnored;
	private int page;
	private int rows;
	private Object routeKey;
	private List<Sort> sortList;
	private List<KV> fixedSortList = new ArrayList<>();
	private List<X> listX = new ArrayList<>();
	private String forceIndex;

	@JsonIgnore
	private transient Parsed parsed;
	@JsonIgnore
	private transient boolean isWhere = true;
	@JsonIgnore
	private transient List<Object> valueList = new ArrayList<Object>();
	@JsonIgnore
	private transient String countDistinct = "COUNT(*) count";
	@JsonIgnore
	private transient String customedResultKey = SqlScript.STAR;

	public Criteria(){}

	@Override
	public List<Object> getValueList() {
		return valueList;
	}

	public Class<?> getClz() {
		return clz;
	}

	public void setClz(Class<?> clz) {
		this.clz = clz;
	}

	public Parsed getParsed() {
		return parsed;
	}

	public void setParsed(Parsed parsed) {
		this.parsed = parsed;
	}

	public String sourceScript() {
		return BeanUtil.getByFirstLower(getClz().getSimpleName());
	}

	public void setCountDistinct(String str){
		this.countDistinct = str;
	}
	public String getCountDistinct(){
		return this.countDistinct;
	}

	public boolean isWhere() {
		return isWhere;
	}

	public void setWhere(boolean where) {
		isWhere = where;
	}

	public List<Sort> getSortList() {
		if (sortList == null || sortList.isEmpty())
			return null;
		Iterator<Sort> ite = sortList.iterator();
		while (ite.hasNext()){
			Sort sort = ite.next();
			if (StringUtil.isNullOrEmpty(sort.getOrderBy())) {
				ite.remove();
			}
		}
		return sortList;
	}

	public void setSortList(List<Sort> sortList) {
		this.sortList = sortList;
	}

	public void setCustomedResultKey(String str){
		if (StringUtil.isNullOrEmpty(str))
			this.customedResultKey = SqlScript.STAR;
		else
			this.customedResultKey = str;
	}

	public String resultAllScript() {
		return customedResultKey;
	}

	public List<KV> getFixedSortList() {
		return fixedSortList;
	}

	public void setTotalRowsIgnored(boolean totalRowsIgnored) {
		isTotalRowsIgnored = totalRowsIgnored;
	}

	@Override
	public boolean isTotalRowsIgnored() {
		return isTotalRowsIgnored;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getForceIndex() {
		return forceIndex;
	}

	public void setForceIndex(String forceIndex) {
		this.forceIndex = forceIndex;
	}

	@Override
	public Object getRouteKey() {
		return routeKey;
	}

	public void setRouteKey(Object routeKey) {
		this.routeKey = routeKey;
	}

	@Override
	public List<X> getListX() {
		return this.listX;
	}
	
	protected void add(X x) {
		this.listX.add(x);
	}

	public boolean isFixedSort() {
		return !this.fixedSortList.isEmpty();
	}


	public Map<String, String> getAliaMap() {
		return null;
	}
	public void paged(Paged paged) {

		this.isTotalRowsIgnored = paged.isTotalRowsIgnored();
		this.page = paged.getPage();
		this.rows = paged.getRows();
		this.sortList = paged.getSortList();
	}


	@Override
	public String toString() {
		return "Criteria{" +
				"isTotalRowsIgnored=" + isTotalRowsIgnored +
				", page=" + page +
				", rows=" + rows +
				", sortList='" + sortList + '\'' +
				", listX=" + listX +
				", forceIndex=" + forceIndex +
				", clz=" + clz +
				'}';
	}

	public static class ResultMappedCriteria extends Criteria implements Serializable{

		private static final long serialVersionUID = -2365612538012282380L;
		private List<String> resultKeyList = new ArrayList<String>();
		private List<FunctionResultKey> resultFuntionList = new ArrayList<>();
		private String groupBy;
		private Distinct distinct;
		private String sourceScript;
		private List<SourceScript> sourceScripts = new ArrayList<>();
		private List<Reduce> reduceList = new ArrayList<>();
		@JsonIgnore
		private transient PropertyMapping propertyMapping;
		@JsonIgnore
		private transient Map<String,String> aliaMap;
		/*
		 * only for oracle
		 */
		@JsonIgnore
		private transient Map<String,String> resultKeyAliaMap = new HashMap<>();

		public Distinct getDistinct() {
			return distinct;
		}

		public List<Reduce> getReduceList() {
			return reduceList;
		}


		public List<SourceScript> getSourceScripts() {
			return sourceScripts;
		}

		public ResultMappedCriteria(){
			super();
		}

		public String getGroupBy() {
			return groupBy;
		}

		public void setGroupBy(String groupBy) {
			if (StringUtil.isNullOrEmpty(this.groupBy)){
				this.groupBy = groupBy;
				return;
			}
			if (this.groupBy.contains(groupBy))
				return;
			this.groupBy = this.groupBy + ", " + groupBy;
		}

		public void setDistinct(Distinct distinct) {
			this.distinct = distinct;
		}

		public PropertyMapping getPropertyMapping() {
			return this.propertyMapping;
		}

		public void setPropertyMapping(PropertyMapping propertyMapping) {
			this.propertyMapping = propertyMapping;
		}

		public Map<String, String> getResultKeyAliaMap() {
			return this.resultKeyAliaMap;
		}

		public Map<String, String> getAliaMap() {
			return aliaMap;
		}

		public void setAliaMap(Map<String, String> aliaMap) {
			this.aliaMap = aliaMap;
		}


		public void setSourceScript(String sourceScript) {
			sourceScript = BeanUtilX.normalizeSql(sourceScript);
			this.sourceScript = sourceScript;
		}
		

		public List<String> getResultKeyList() {
			return resultKeyList;
		}

		public List<FunctionResultKey> getResultFuntionList() {
			return resultFuntionList;
		}

		@Override
		public Class<?> getClz() {
			return super.clz == null ? Map.class : super.clz;
		}

		@Override
		public String sourceScript() {
			if (sourceScript == null) {
				return BeanUtil.getByFirstLower(super.getClz().getSimpleName());
			} else {
				return sourceScript;
			}
		}

		public void adpterResultScript() {
			if (Objects.nonNull(super.customedResultKey)&&!super.customedResultKey.equals(SqlScript.STAR)){
				return;
			}else {
				int size = 0;
				String column = "";
				if (resultKeyList.isEmpty()) {
					column += (SqlScript.SPACE + SqlScript.STAR + SqlScript.SPACE);
				} else {
					size = resultKeyList.size();
					for (int i = 0; i < size; i++) {
						column = column + SqlScript.SPACE + resultKeyList.get(i);
						if (i < size - 1) {
							column += SqlScript.COMMA;
						}
					}
				}
				super.customedResultKey = column;
			}

		}


		@Override
		public String toString() {
			return "ResultMappedCriteria{" +
					"resultKeyList=" + resultKeyList +
					", sourceScript='" + sourceScript + '\'' +
					", distinct=" + distinct +
					", groupBy='" + groupBy + '\'' +
					", reduceList=" + reduceList +
					", aliaMap=" + aliaMap +
					'}';
		}
	}

	
	public static class X {

		private ConjunctionAndOtherScript conjunction;
		private PredicateAndOtherScript predicate;
		private String key;
		private Object value;
		private List<X> subList;
		private X parent;
		private transient String script;
		public X(){}
		public ConjunctionAndOtherScript getConjunction() {
			return conjunction;
		}
		public void setConjunction(ConjunctionAndOtherScript conjunction) {
			this.conjunction = conjunction;
		}
		public PredicateAndOtherScript getPredicate() {
			return predicate;
		}
		public void setPredicate(PredicateAndOtherScript predicate) {
			this.predicate = predicate;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		public List<X> getSubList() {
			return subList;
		}
		public void setSubList(List<X> subList) {
			this.subList = subList;
		}
		public X getParent() {
			return parent;
		}
		public void setParent(X parent) {
			this.parent = parent;
		}
		public String getScript() {
			return script;
		}
		public void setScript(String script) {
			this.script = script;
		}

		@Override
		public String toString() {
			return "X{" +
					"conjunction=" + conjunction +
					", predicate=" + predicate +
					", key=" + key +
					", value=" + value +
					", subList=" + subList +
					", script=" + script +
					'}';
		}
	}


}