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
package x7.repository.cache;

import x7.core.util.JsonX;
import x7.core.util.StringUtil;
import x7.core.web.Page;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class ObjectUtil {

	public static byte[] toBytes(Object obj) throws UnsupportedEncodingException {
		if (obj == null)
			return null;
		String str = JsonX.toJson(obj);
		byte[] bytes = null;
		bytes = str.getBytes("UTF-8");
		return bytes;
	}

	public static <T> T toObject(byte[] bytes, Class<T> clz) {
		if (bytes == null)
			return null;
		try {
			String str = new String(bytes, "UTF-8");
			T t = JsonX.toObject(str, clz);
			return t;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	public static List<Map<String,Object>> toMapList(byte[] bytes) {
		if (bytes == null)
			return null;
		try {
			String str = new String(bytes, "UTF-8");
			return (List)JsonX.toList(str, Map.class);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}


	public static <T> List<T> toList(byte[] bytes, Class<T> clz) {
		if (bytes == null)
			return null;
		try {
			String str = new String(bytes, "UTF-8");
			List<T> list = JsonX.toList(str, clz);
			return list;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static <T> Page<T> toPagination(String json) {
		if (StringUtil.isNullOrEmpty(json))
			return null;
		try {

			Page<T> pagination = JsonX.toObject(json, Page.class);

			return pagination;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
