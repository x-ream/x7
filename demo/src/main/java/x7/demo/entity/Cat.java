package x7.demo.entity;


import io.xream.sqli.annotation.X;
import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Cat implements Serializable {

	private static final long serialVersionUID = 5708147778966785698L;

	@X.Key
	private long id;
	private long userId;
	@X.Mapping("cat_type")
	private String type;
	private String name;
	private String taxType;
	private long dogId;
	private long test;
	private List<Long> list;
	private TestBoo testBoo;
	private List<String> testList;
	private LocalDate createAt;
	private Boolean isDone;

	private transient Map<Object,Object> viewMap;

	public String getName(){
		return MapUtils.getString(viewMap,""+id);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public long getDogId() {
		return dogId;
	}

	public void setDogId(long dogId) {
		this.dogId = dogId;
	}

	public long getTest() {
		return test;
	}

	public void setTest(long test) {
		this.test = test;
	}

	public List<Long> getList() {
		return list;
	}

	public void setList(List<Long> list) {
		this.list = list;
	}

	public TestBoo getTestBoo() {
		return testBoo;
	}

	public void setTestBoo(TestBoo testBoo) {
		this.testBoo = testBoo;
	}

	public List<String> getTestList() {
		return testList;
	}

	public void setTestList(List<String> testList) {
		this.testList = testList;
	}

	public LocalDate getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDate createAt) {
		this.createAt = createAt;
	}

	public Boolean getIsDone() {
		return isDone;
	}

	public void setIsDone(Boolean done) {
		isDone = done;
	}

	@Override
	public String toString() {
		return "Cat{" +
				"id=" + id +
				", userId=" + userId +
				", type='" + type + '\'' +
				", name='" + name + '\'' +
				", taxType='" + taxType + '\'' +
				", dogId=" + dogId +
				", test=" + test +
				", list=" + list +
				", testBoo=" + testBoo +
				", testList=" + testList +
				", viewMap=" + viewMap +
				", createAt=" + createAt +
				'}';
	}
}
