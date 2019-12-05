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

import x7.core.repository.X;
import x7.core.search.TagParsed;
import x7.core.util.BeanUtilX;
import x7.core.util.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Parsed {

	private boolean isTransformable = false;
	private String transformedAlia;
	private Parsed parsedTransformed;

	private boolean isChecked = false;

	private Class clz;
	private String tableName;
	private String originTable;
	private boolean isNoSpec = true;

	private final Map<Integer,String> keyMap = new HashMap<Integer,String>();
	private final Map<Integer,Field> keyFieldMap = new HashMap<Integer,Field>();
	
	private List<BeanElement> beanElementList;
	
	private Map<String, BeanElement> elementMap = new HashMap<String, BeanElement>();
	private Map<String,String> propertyMapperMap = new HashMap<String,String>();
	private Map<String,String> mapperPropertyMap = new HashMap<String,String>();
	
	private boolean isNoCache;
	
	private List<String> keywordsList = new ArrayList<String>();
	
	private boolean isSearchable;
	
	private Map<String, TagParsed> tagMap = new HashMap();

	public Parsed getParsedTransformed() {
		return parsedTransformed;
	}

	public void setParsedTransformed(Parsed parsedTransformed) {
		this.parsedTransformed = parsedTransformed;
	}

	public Class getClz() {
		return clz;
	}

	public void setClz(Class clz) {
		this.clz = clz;
	}
	
	public Parsed(Class clz){
		this.clz = clz;
	}

	public String getId(){
		return String.valueOf(keyMap.get(X.KEY_ONE));
	}
	
	public BeanElement getElement(String property){
		return elementMap.get(property);
	}

	public Map<String, BeanElement> getElementMap() {
		return elementMap;
	}

	public Map<Integer, String> getKeyMap() {
		return keyMap;
	}
	
	public boolean contains(String property) {
		return this.elementMap.containsKey(property);
	}

	public Map<Integer, Field> getKeyFieldMap() {
		return keyFieldMap;
	}
	
	public Field getKeyField(int index){
		return keyFieldMap.get(index);
	}

	public String getKey(int index){
		if (keyMap.isEmpty() && index == X.KEY_ONE) //DEFAULT
			return "id";
		return keyMap.get(index);
	}

	public List<BeanElement> getBeanElementList() {
		return beanElementList;
	}

	public void setBeanElementList(List<BeanElement> beanElementList) {
		this.beanElementList = beanElementList;
	}

	public String getOriginTable() {
		return originTable;
	}

	public void setOriginTable(String originTable) {
		this.originTable = originTable;
	}

	public void reset(List<BeanElement> beanElementList){
		this.beanElementList = beanElementList;
		this.propertyMapperMap.clear();
		this.mapperPropertyMap.clear();
		this.elementMap.clear();
		for (BeanElement e : this.beanElementList){
			String property = e.getProperty();
			String mapper = e.getMapper();
			this.elementMap.put(property, e);
			this.propertyMapperMap.put(property, mapper);
			this.mapperPropertyMap.put(mapper, property);
		}
	}
	
	public boolean isChecked(){
		return this.isChecked;
	}
	
	public void checked(){
		this.isChecked = true;
	}

	public String getTableName(String alia) {
		if (StringUtil.isNullOrEmpty(alia))
			return tableName;
		if (! alia.toLowerCase().equals(getClzName().toLowerCase()))
			return alia;
		return tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = BeanUtilX.filterSQLKeyword(tableName);
	}
	
	public String getClzName() {
		return this.clz.getSimpleName();
	}

	public boolean isNoCache() {
		return isNoCache;
	}

	public void setNoCache(boolean isNoCache) {
		this.isNoCache = isNoCache;
	}


	public String getTransformedAlia() {
		return this.transformedAlia;
	}

	public void setTransforemedAlia(String alia) {
		this.transformedAlia = alia;
	}

	public List<String> getKeywordsList() {
		return keywordsList;
	}

	public void setKeywordsList(List<String> keywordsList) {
		this.keywordsList = keywordsList;
	}
	
	public boolean isSearchable() {
		return isSearchable;
	}

	public void setSearchable(boolean isSearchable) {
		this.isSearchable = isSearchable;
	}

	public String[] getKeywardsArr(){

		String[] keywordsArr = new String[this.keywordsList.size()];
		this.keywordsList.toArray(keywordsArr);
		
		return keywordsArr;
	}

	public Map<String, TagParsed> getTagMap() {
		return tagMap;
	}

	public void setTagMap(Map<String, TagParsed> tagMap) {
		this.tagMap = tagMap;
	}

	public String getMapper(String property) {
		return propertyMapperMap.get(property);
	}
	
	public String getProperty(String mapper){
		return mapperPropertyMap.get(mapper);
	}

	public Map<String, String> getPropertyMapperMap() {
		return propertyMapperMap;
	}

	public Map<String, String> getMapperPropertyMap() {
		return mapperPropertyMap;
	}
	
	public boolean isNoSpec() {
		return isNoSpec;
	}

	public void setNoSpec(boolean isNoSpec2) {
		this.isNoSpec = isNoSpec2;
	}

	@Override
	public String toString() {
		return "Parsed{" +
				"isTransformable=" + isTransformable +
				", transformedAlia='" + transformedAlia + '\'' +
				", parsedTransformed=" + parsedTransformed +
				", isChecked=" + isChecked +
				", clz=" + clz +
				", tableName='" + tableName + '\'' +
				", originTable='" + originTable + '\'' +
				", isNoSpec=" + isNoSpec +
				", keyMap=" + keyMap +
				", keyFieldMap=" + keyFieldMap +
				", beanElementList=" + beanElementList +
				", elementMap=" + elementMap +
				", propertyMapperMap=" + propertyMapperMap +
				", mapperPropertyMap=" + mapperPropertyMap +
				", isNoCache=" + isNoCache +
				", keywordsList=" + keywordsList +
				", isSearchable=" + isSearchable +
				", tagMap=" + tagMap +
				'}';
	}
}
