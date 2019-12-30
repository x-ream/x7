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

import com.fasterxml.jackson.annotation.JsonIgnore;
import x7.core.util.BeanUtil;
import x7.core.util.BeanUtilX;
import x7.core.util.StringUtil;
import x7.core.web.Paged;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author sim
 *
 */
public class Criteria implements CriteriaCondition, Paged, Serializable {

	private static final long serialVersionUID = 7088698915888081349L;

	private Class<?> clz;
	private boolean isScroll;
	private int page;
	private int rows;
	private List<Sort> sortList;
	private List<KV> fixedSortList = new ArrayList<>();
	private List<X> listX = new ArrayList<>();
	private DataPermission dataPermission;//String,Or List<String>   LikeRight | In

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

	public void setValueList(List<Object> valueList) {
		this.valueList = valueList;
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

	public void setFixedSortList(List<KV> fixedSortList) {
		this.fixedSortList = fixedSortList;
	}

	public boolean isScroll() {
		return isScroll;
	}

	public void setScroll(boolean isScroll) {
		this.isScroll = isScroll;
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

	@Override
	public List<X> getListX() {
		return this.listX;
	}
	
	protected void add(X x) {
		this.listX.add(x);
	}

	public DataPermission getDataPermission() {
		return dataPermission;
	}

	public void setDataPermission(DataPermission dataPermission) {
		this.dataPermission = dataPermission;
	}

	public boolean isFixedSort() {
		return !this.fixedSortList.isEmpty();
	}


	public Map<String, String> getAliaMap() {
		return null;
	}
	public void paged(Paged paged) {

		this.isScroll = paged.isScroll();
		this.page = paged.getPage();
		this.rows = paged.getRows();
		this.sortList = paged.getSortList();
	}


	@Override
	public String toString() {
		return "Criteria{" +
				"isScroll=" + isScroll +
				", page=" + page +
				", rows=" + rows +
				", sortList='" + sortList + '\'' +
				", valueList=" + valueList +
				", listX=" + listX +
				", dataPermission=" + dataPermission +
				", isWhere=" + isWhere +
				", countDistinct='" + countDistinct + '\'' +
				", customedResultKey='" + customedResultKey + '\'' +
				", clz=" + clz +
				'}';
	}

	public static class ResultMappedCriteria extends Criteria implements Serializable{

		private static final long serialVersionUID = -2365612538012282380L;
		private List<String> resultKeyList = new ArrayList<String>();
		private String sourceScript;
		private Distinct distinct;
		private String groupBy;
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

		public void setReduceList(List<Reduce> reduceList) {
			this.reduceList = reduceList;
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

		public void setResultKeyAliaMap(Map<String, String> aliaMap) {
			this.resultKeyAliaMap = aliaMap;
		}

		public String getResultScript() {
			if (resultKeyList.isEmpty()){
				return SqlScript.STAR;
			}else{
				StringBuilder sb = new StringBuilder();
				int i = 0;
				int size = resultKeyList.size() - 1;
				for (String str : resultKeyList){
					String mapper = getPropertyMapping().mapper(str);
					sb.append(mapper);
					if (i < size){
						sb.append(SqlScript.COMMA);
					}
					i++;
				}
				return sb.toString();
			}
		}

		public void setSourceScript(String sourceScript) {
			sourceScript = BeanUtilX.normalizeSql(sourceScript);
			this.sourceScript = sourceScript;
		}
		

		public List<String> getResultKeyList() {
			return resultKeyList;
		}

		public void setResultKeyList(List<String> columnList) {
			this.resultKeyList = columnList;
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


		@Override
		public String resultAllScript() {
			if (Objects.nonNull(super.customedResultKey)&&!super.customedResultKey.equals(SqlScript.STAR)){
				return super.customedResultKey;
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
				return column;
			}

		}


		@Override
		public String toString() {
			return "ResultMapped{" +
					"resultKeyList=" + resultKeyList +
					", sourceScript='" + sourceScript + '\'' +
					", criteria='" + super.toString() + '\'' +
					'}';
		}

	}

	
	public static class X {
		private static final long serialVersionUID = 7088698915888083256L;
		private Conjunction conjunction;
		private Predicate predicate;
		private String key;
		private Object value;
		private List<X> subList;
		private X parent;
		private transient String script;
		public X(){}
		public Conjunction getConjunction() {
			return conjunction;
		}
		public void setConjunction(Conjunction conjunction) {
			this.conjunction = conjunction;
		}
		public Predicate getPredicate() {
			return predicate;
		}
		public void setPredicate(Predicate predicate) {
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


	/**
	 *
	 * 查询条件仅仅来自主表, 但要返回同一个域里的相关表结果<br>
	 * 不需要Fetched查询<br>
	 * 支持L2Cache<br>
	 */
	public static class DomainObjectCriteria<T,WITH> extends Criteria implements Serializable {

		private static final long serialVersionUID = -886967622448599208L;
		private Class withClz;
		private Class relativeClz;
		private String mainPropperty;
		private String withProperty;
		private List<? extends  Object> knownMainIdList;


		public DomainObjectCriteria(){
			super();
		}

		public Class getWithClz() {
			return withClz;
		}

		public void setWithClz(Class withClz) {
			this.withClz = withClz;
		}

		public Class getRelativeClz() {
			return relativeClz;
		}

		public void setRelativeClz(Class relativeClz) {
			this.relativeClz = relativeClz;
		}

		public String getMainPropperty() {
			return mainPropperty;
		}

		public void setMainPropperty(String mainPropperty) {
			this.mainPropperty = mainPropperty;
		}

		public String getWithProperty() {
			return withProperty;
		}

		public void setWithProperty(String withProperty) {
			this.withProperty = withProperty;
		}

		public List<? extends  Object> getKnownMainIdList() {
			return knownMainIdList;
		}

		public void setKnownMainIdList(List<? extends  Object> knownMainIdList) {
			this.knownMainIdList = knownMainIdList;
		}

		@Override
		public String toString() {
			return "DomainObjectCriteria{" +
					"withClz=" + withClz +
					", relativeClz=" + relativeClz +
					", mainPropperty='" + mainPropperty + '\'' +
					", withProperty='" + withProperty + '\'' +
					", knownMainIdList=" + knownMainIdList +
					'}';
		}
	}


}