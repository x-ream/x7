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

public interface SqlScript {

	String SELECT = "SELECT";
	String DISTINCT = "DISTINCT";
	String WHERE = " WHERE ";
	String FROM = "FROM";
	String LIMIT = " LIMIT ";
	String SET = " SET ";
	String UPDATE = "UPDATE";
	String IN = " IN ";

	String AS = " AS ";

	String NONE = "";
	String SPACE = " ";
	String PLACE_HOLDER = "?";
	String EQ_PLACE_HOLDER = " = ?";
	String LIKE_HOLDER = "%";
	String POINT = ".";
	String COMMA = ",";
	String STAR = "*";
	String UNDER_LINE = "_";
	String LEFT_PARENTTHESIS = "(";
	String RIGHT_PARENTTHESIS = ")";
	String WELL_NO = "#";
	String SINGLE_QUOTES = "'";
	String KEY_SQL = "`";

	String sql();
}
