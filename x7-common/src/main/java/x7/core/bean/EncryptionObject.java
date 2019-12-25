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

import org.apache.commons.codec.binary.Base64;
import x7.core.util.JsonX;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class EncryptionObject<T> {

	private String text;
	private Class<T> clz;
	private transient T obj;
	
	public Class<T> getClz() {
		return clz;
	}
	public void setClz(Class<T> clz) {
		this.clz = clz;
	}
	public String getText() {
		if (!Objects.isNull(text))
			return text;
		try {
			String json = JsonX.toJson(obj);
			byte[] bytes = Base64.encodeBase64(json.getBytes("UTF-8"));
			text = new String(bytes,"UTF-8");
			return text;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

	}
	public void setText(String text) {
		this.text = text;
	}
	public T getObj() {
		if (Objects.isNull(obj) && !Objects.isNull(text)){
			byte[] bytes = Base64.decodeBase64(text);
			try {
				String json = new String(bytes,"UTF-8");
				obj = (T) JsonX.toObject(json, clz);
				return obj;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
		return obj;
	}
	public void setObj(T obj) {
		this.obj = obj;
		this.clz = (Class<T>) obj.getClass();
	}
	@Override
	public String toString() {
		return ""+getObj();
	}
	
}
