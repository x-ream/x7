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
package x7.core.web;

import x7.core.bean.Sort;
import x7.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PagedRo implements Paged{

	private boolean isScroll;
	private int page;
	private int rows;
	private String orderBy;
	private Direction direction;
	private List<Sort> sortList;
	public boolean isScroll() {
		return isScroll;
	}
	public void setScroll(boolean isScroll) {
		this.isScroll = isScroll;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction sc) {
		this.direction = sc;
	}

	public List<Sort> getSortList() {
		if (sortList != null && !sortList.isEmpty())
			return sortList;
		if (StringUtil.isNotNull(orderBy)){
			if(sortList == null){
				sortList = new ArrayList<>();
			}
			Direction d = this.direction == null ? Direction.DESC : this.direction;
			String[] arr = orderBy.split(",");
			for (String str : arr) {
				Sort sort = new Sort(str.trim(), d);
				sortList.add(sort);
			}
		}
		return sortList;
	}

	public void setSortList(List<Sort> sortList) {
		this.sortList = sortList;
	}

	@Override
	public String toString() {
		return "PagedRo{" +
				"isScroll=" + isScroll +
				", page=" + page +
				", rows=" + rows +
				", orderBy='" + orderBy + '\'' +
				", direction=" + direction +
				", sortList=" + sortList +
				'}';
	}
}
