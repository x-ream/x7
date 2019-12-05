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

import x7.core.repository.ISerialWR;

import java.util.HashMap;
import java.util.Map;

public class BeanSerial {

	private static Map<String, ISerialWR> map = new HashMap<String, ISerialWR>();

	public static Map<String, ISerialWR> getMap() {
		return map;
	}

	public static void setMap(Map<String, ISerialWR> mapx) {
		map = mapx;
	}
	
	public static ISerialWR get(String clzName){
		ISerialWR wr = map.get(clzName);
		
		if (wr == null) {
			try{
				wr = (ISerialWR) Class.forName(clzName + "WR").newInstance();
				System.out.println("wr = " + wr);
				map.put(clzName, wr);
			}catch (Exception e) {
				map.put(clzName, null);
			}
		}
		
		return wr;
	}
}
