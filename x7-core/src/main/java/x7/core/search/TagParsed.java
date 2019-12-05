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
import java.lang.reflect.Field;

public class TagParsed implements Serializable{

	private static final long serialVersionUID = 7631076184961243538L;

	private Class type;
	private String tagKey;
	private Field field;
	
	
	public Class getType() {
		return type;
	}
	public void setType(Class type) {
		this.type = type;
	}


	public String getTagKey() {
		return tagKey;
	}
	public void setTagKey(String tagKey) {
		this.tagKey = tagKey;
	}
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
	@Override
	public String toString() {
		return "TagParsed [type=" + type + ", tagKey=" + tagKey 
				+ "]";
	}
}
