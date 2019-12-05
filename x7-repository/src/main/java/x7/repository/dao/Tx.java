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
package x7.repository.dao;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * 
 * Tx.begin(); <br>
 * (ok) Tx.commit(); <br>
 * (exception) Tx.rollback(); <br>
 * <br>
 * 
 * @author Sim
 *
 */
public class Tx {

	private static Map<String, TransactionStatus> map = new ConcurrentHashMap<>();

	private static PlatformTransactionManager tm;
	protected static void init(DataSourceTransactionManager dstm){
		tm = dstm;
	}

	public static String getKey(){
		long threadId = Thread.currentThread().getId();
		String key = String.valueOf(threadId);
		return key;
	}


	public static void begin(){
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		TransactionStatus status = tm.getTransaction(def);

		String key = getKey();
		map.put(key,status);

	}

	public static void commit(){

		String key = getKey();
		TransactionStatus status = map.remove(key);

		tm.commit(status);
	}

	public static void rollback(){

		String key = getKey();
		TransactionStatus status = map.remove(key);

		tm.rollback(status);
	}
}
