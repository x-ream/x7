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
package x7.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyUtil {

	public static List<String> getKeyList(String str) {

		List<String> list = null;
		if (str.contains(".")) {
			String[] arr = str.split("\\.");
			list = Arrays.asList(arr);

		} else if (str.contains("/")) {
			if (str.startsWith("/")) {
				str = str.substring(1);
			}
			String[] arr = str.split("\\/");
			list = Arrays.asList(arr);
		} else {
			list = new ArrayList<String>();
			list.add(str);
		}

		return list;
	}

	/**
	 * 仅仅用于JAVA类名做为KEY时的转换
	 * @param clz
	 * @return
	 */
	public static String getKey(Class clz) {

		String key = clz.getName();
		if (key.contains(".")) {
			key = key.replace(".", "_");
		}

		return key;
	}
}
