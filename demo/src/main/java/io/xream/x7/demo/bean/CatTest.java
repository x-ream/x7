package io.xream.x7.demo.bean;

import x7.core.repository.X;

import java.sql.Timestamp;

public class CatTest {

	@X.Key
	private long id;
	private long dogId;
	private String catFriendName;
	private Timestamp time;
	private String type;
	private Boolean isCat;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getDogId() {
		return dogId;
	}
	public void setDogId(long dogId) {
		this.dogId = dogId;
	}
	public String getCatFriendName() {
		return catFriendName;
	}
	public void setCatFriendName(String catFriendName) {
		this.catFriendName = catFriendName;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIsCat() {
		return isCat;
	}

	public void setIsCat(Boolean cat) {
		isCat = cat;
	}

	@Override
	public String toString() {
		return "CatTest [id=" + id + ", dogId=" + dogId + ", catFriendName=" + catFriendName + ", time=" + time + "]";
	}

}
