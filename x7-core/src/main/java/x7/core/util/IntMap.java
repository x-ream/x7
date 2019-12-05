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

import java.io.Serializable;


/**
 * 简单的int map<br>
 * 因为是int型,无法作null判断, 设计上用"-1"作为保留数字,key 和 value都不能为-1;<br>
 * 如果返回-1,表示没查到<br>
 * 当元数过多时, 请不要使用, 最好不要超过300个
 * @author Sim
 *
 */
public class IntMap implements Serializable{

	private static final long serialVersionUID = 270700123037753057L;
	
	private int capacity = 1;
	
	private final static int KEY_BASE = -1;
	private final static int VALUE_BASE = 0;
	
	private int[] keys;
	private int[] values;
	
	public IntMap(){
		this.keys = new int[1];
		this.values = new int[1];
		this.keys[0] = KEY_BASE;
		this.values[0] = VALUE_BASE;
	}
	
	/**
	 * 如果未来size会超过10个，请使用<br>

	 * @param capacity
	 */
	public IntMap(int capacity) {
		if (capacity < 1){
			System.out.println(" capacity必需大于0 ");
		}
		this.capacity = capacity;
		create(capacity);
	}
	
	public int[] keys(){
		int size = size();
		int[] newKeys = new int[size];
		int j=0;
		int length = this.keys.length;
		for (int i=0; i<length; i++){
			if (this.keys[i]!=-1){
				newKeys[j++] = this.keys[i];
			}
		}
		return newKeys;
	}
	
	public int[] values(){
		int size = size();
		int[] newValues = new int[size];
		int j=0;
		int length = this.values.length;
		for (int i=0; i<length; i++){
			if (this.values[i]!=-1){
				newValues[j++] = this.values[i];
			}
		}
		return newValues;
	}
	
	/**
	 * 返回对应的VALUE,如果没有则返回0
	 * @param key
	 * @return
	 */
	public int get(int key) {
		if (key == -1){
			System.out.println( " -1 is kept ");
		}
		
		int index = -1;
		int size = keys.length;
		for (int i=0; i < size; i++){
			if (key == keys[i]){
				index = i;
				break;
			}
		}
		
		if (index == -1)
			return 0;
		
		return values[index];
		
	}
	/**
	 * key 或 value 为 -1 是，则抛出异常;<br>
	 * 如果计算出的value可能为-1<br>
	 * 请硬编码解决<br>
	 * @param key
	 * @param value
	 */
	public void put(int key, int value) {
		if (key == -1 || value == -1){
			 System.out.println( " -1 is kept ");
		}
		
		
		int size = keys.length;
		
		int index = -1;
		for (int i=0; i < size; i++){
			if (keys[i]==key || keys[i] == -1){
				keys[i]=key;
				index = i;
				break;
			}
		}
		
		if (index == -1){
			int[] newKeys = new int[size+1];
			int[] newValues = new int[size+1];
			System.arraycopy(keys, 0, newKeys, 0, size);
			System.arraycopy(values, 0, newValues, 0, size);
			this.keys = newKeys;
			this.values = newValues;
			index = size;
			this.keys[index] = key;
		}
		
		values[index] = value;
	}
	
	public void putAll(IntMap map){
		for (int key : map.keys()) {
			int origin = this.get(key);
			this.put(key, origin + map.get(key));
		}
	}
	
	public int remove(int key) throws Exception{
		if (key == -1)
			throw new Exception( " -1 is kept ");		
		
		int index = -1;
		int size = keys.length;
		for (int i=0; i < size; i++){
			if (key == keys[i]){
				index = i;
				break;
			}
		}
		
		if (index == -1)
			return -1;
		
		int result = values[index];
		
		/*
		 * 移除
		 */
		this.keys[index] = -1;
		this.values[index] = -1;
		
		/*
		 * 如果MAP过长，则做实际的空间清楚
		 */
		int keySize = keys.length;
		int length = 0;
		for (int i=0; i<keySize; i++){
			if (keys[i] == -1)
				length++;
		}
		if (length > capacity * 2){
			size = size();
			size = size < capacity ? capacity : size; // size 不能小于capacity
			int[] newKeys = new int[size];
			int[] newValues = new int[size];
			int j = 0;
			/*
			 * 复制数据
			 */
			for (int i=0; i<keySize; i++){
				if (keys[i] != -1){
					newKeys[j] = keys[i];
					newValues[j] = values[i];
					j++;
				}
			}
			/*
			 * 如果有效长度小于capacity,则后面无效的部分填充-1
			 */
			for (; j < size; j++){
				newKeys[j] = -1;
				newValues[j] = -1;
			}
			this.keys = newKeys;
			this.values = newValues;
		}

		return result;
		
	}
	
	public boolean containsKey(int key){
		int size = keys.length;
		for (int i=0; i<size; i++){
			if (keys[i] == key)
				return true;
		}
		return false;
	}
	
	public boolean containsValue(int value){
		int size = values.length;
		for (int i=0; i<size; i++){
			if (values[i] == value)
				return true;
		}
		return false;
	}
	
	public int size(){
		int result = 0;
		int size = keys.length;
		for (int i=0; i<size; i++){
			if (keys[i]!=-1)
				result++;
		}
		return result;
	}
	
	public void claer(){
		create(capacity);
	}
	
	private void create(int capacity){
		int size = capacity;
		this.keys = new int[size];
		this.values = new int[size];
		for (int i=0; i<size; i++){
			keys[i] = -1;
			values[i] = -1;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{" );
		int size = size();
		for (int i=0; i<size; i++){
			if (keys[i]!=-1){
				sb.append("\"").append(keys[i]).append("\"").append(":").append("\"").append(values[i]).append("\"");
				if (i < size-1)
					sb.append(",");
			}
		}
		sb.append("}");
		return sb.toString();
	}


}
