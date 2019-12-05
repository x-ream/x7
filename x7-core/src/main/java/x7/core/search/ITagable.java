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

import java.util.ArrayList;
import java.util.List;

public interface ITagable {

	
	public static String makeTag(List<Long> tagIdList){
		if (tagIdList == null || tagIdList.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		int size = tagIdList.size();
		for (int i=0; i<size; i++){
			Long id = tagIdList.get(i);
			sb.append(id);
			if (i < size -1){
				sb.append("_");
			}
		}
		return sb.toString();
		
	}
	
	public static List<Long> listTagId(String str){
		List<Long> list = new ArrayList<Long>();
		if (str == null)
			return list;
		String[] arr = str.split("_");
		for (String ele : arr){
			list.add(Long.parseLong(ele));
		}
		return list;
	}
}
