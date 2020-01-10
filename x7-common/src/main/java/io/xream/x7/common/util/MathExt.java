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
package io.xream.x7.common.util;

import java.util.Random;

public class MathExt {

	private MathExt(){}

	private final static Random random = new Random();

	/**
	 * 
	 * @param i
	 * @param offset
	 */
	public static int moveLeft(int i, int offset) {
		return i << offset;
	}

	/**
	 *
	 */
	public static int random(int i) {
		return random.nextInt(i);
	}

	/**
	 *
	 */
	public static int random(int min, int i) {
		return min + random.nextInt(i - min);
	}

	/**
	 * 根据权重获得随机索引
	 * 
	 * @param weights
	 * @return weights [] index, -1表示没有(权重总和为0则表示没有)
	 */
	public static int randomByWeights(int[] weights) {
		int length = weights.length;
		int totalWeight = 0;
		for (int i = 0; i < length; i++) {
			totalWeight += weights[i];
		}
		
		if (totalWeight == 0)
			return -1;
		
		int hit = random(totalWeight);

		totalWeight = 0;
		for (int i = 0; i < length; i++) {
			totalWeight += weights[i];
			if (totalWeight >= hit)
				return i;
		}

		return 0;
	}

	/**
	 * 
	 * @param rate
	 * @return
	 */
	public static boolean isHitByRate(int rate) {
		if (rate < 1)
			return false;
		if (rate >= 100)
			return true;
		return rate >= Math.random() * 100 + 1;
	}

	/**
	 * 
	 * @param rate
	 *            like,20% 0.2
	 * @return
	 */
	public static boolean isHitByRate(double rate) {
		if (rate <= 0)
			return false;
		if (rate > 1)
			return true;
		return rate >= Math.random();
	}

	/**
	 * 
	 * @param low
	 * @param high
	 * @param i
	 * @return 不小于L,不大于H
	 */
	public static int LH(int low, int high, int i) {
		if (i < low)
			return low;
		if (i > high)
			return high;
		return i;
	}

	/**
	 * 
	 * @param low
	 * @param high
	 * @param i
	 * @return 不小于L,不大于H
	 */
	public static double LH(double low, double high, double i) {
		if (i < low)
			return low;
		if (i > high)
			return high;
		return i;
	}

	/**
	 * 
	 * @param oldValue
	 *            原始值
	 * @param newValue
	 *            新值
	 * @param replacedTimes
	 *            替换次数
	 * @return
	 */
	public static int sumReplace(int oldValue, int newValue, int replacedTimes) {
		return (newValue - oldValue) * replacedTimes;
	}

	/**
	 * 等级区间定位<br>
	 * 不在区间内，返回-1
	 * 
	 * @param level
	 * @param levelSections
	 * @return
	 */
	public static int fixLevel(int level, int[] levelSections) {
		if (levelSections == null)
			return -1;
		int length = levelSections.length;
		for (int i = length - 1; i >= 0; i--) {
			if (level >= levelSections[i])
				return levelSections[i];
		}
		return -1;
	}
	
	/**
	 * @param n
	 * @param dv
	 * @param factor
	 * @return
	 */
	public static int dividePlusOneOnFactor(double n, double dv, double factor){
		double t = n / dv;
		int r = (int) t;
		if (t - r >= factor)
			return r + 1;
		return r;
	}

}
