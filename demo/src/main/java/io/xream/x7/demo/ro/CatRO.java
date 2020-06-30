package io.xream.x7.demo.ro;

import io.xream.x7.common.web.TokenedAndPagedRo;
import io.xream.x7.common.web.MapResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CatRO extends TokenedAndPagedRo implements MapResult {

	private String catFriendName;
	
	private Map<String, Object> resultKeyMap = new HashMap<String,Object>();
	
	
	public String getCatFriendName() {
		return catFriendName;
	}
	public void setCatFriendName(String catFriendName) {
		this.catFriendName = catFriendName;
	}
	public void setResultKeyMap(Map<String, Object> resultKeyMap) {
		this.resultKeyMap = resultKeyMap;
	}


	public Map<String, Object> getResultKeyMap() {
		return resultKeyMap;
	}

	private String[] resultKeys;

	public String[] getResultKeys() {
		return resultKeys;
	}

	public void setResultKeys(String[] arr){
		this.resultKeys = arr;
	}

	@Override
	public String toString() {
		return "CatRO{" +
				"catFriendName='" + catFriendName + '\'' +
				", resultKeyMap=" + resultKeyMap +
				", resultKeys=" + Arrays.toString(resultKeys) +
				'}';
	}
}
