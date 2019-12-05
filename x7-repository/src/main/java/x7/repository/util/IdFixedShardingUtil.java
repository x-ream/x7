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
package x7.repository.util;

import java.util.ArrayList;
import java.util.List;

public class IdFixedShardingUtil {

	public static List<long[]> listBeginEnd(long minId, long maxId, long count, long rowsOfSharding){
		
		List<long[]> list = new ArrayList<long[]>();
		
		long testCount = maxId - minId + 2;
		
		System.out.println("__testCount = " + testCount);
		
		long size = count / rowsOfSharding + (count%rowsOfSharding == 0 ? 0 : 1);
		
		System.out.println("__size = " + size);
		
		long shardingNum = size == 0 ? 1 : testCount / size;
		
		System.out.println("__ShardingNum = " + shardingNum);
		
		for (int i=0; i<size; i++){
			long begin = minId + i * shardingNum;
			long end = minId + (i + 1) * shardingNum - 1;
			
			end = maxId < end ? maxId : end;
			
			long[] be = new long[2];
			be[0] = begin;
			be[1] = end;
			
			if (i == size-1){
				be[1] += end;
			}
			
			list.add(be);
			System.out.println("_________begin: " + be[0] + ", end: " + be[1]);
		}
		
		return list;
	}


}
