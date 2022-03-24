package x7.demo.entity;


import io.xream.sqli.annotation.X;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class Cat implements Serializable {

	private static final long serialVersionUID = 5708147778966785698L;

	@X.Key
	private Long id;
	private Long userId;
	@X.Mapping("cat_type")
	private String type;
	private String name;
	private String taxType;
	private Long dogId;
	private Long test;
	private List<Long> list;
	private TestBoo testBoo;
	private List<String> testList;
	private LocalDate createAt;
	private Boolean isDone;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public Long getDogId() {
		return dogId;
	}

	public void setDogId(Long dogId) {
		this.dogId = dogId;
	}

	public Long getTest() {
		return test;
	}

	public void setTest(Long test) {
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
				", createAt=" + createAt +
				", isDone=" + isDone +
				'}';
	}

}
