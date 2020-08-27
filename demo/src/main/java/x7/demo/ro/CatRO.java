package x7.demo.ro;


import io.xream.x7.base.web.TokenedAndPagedRo;

import java.util.Arrays;

public class CatRO extends TokenedAndPagedRo  {

	private String catFriendName;
	
	public String getCatFriendName() {
		return catFriendName;
	}
	public void setCatFriendName(String catFriendName) {
		this.catFriendName = catFriendName;
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
				", resultKeys=" + Arrays.toString(resultKeys) +
				'}';
	}
}
