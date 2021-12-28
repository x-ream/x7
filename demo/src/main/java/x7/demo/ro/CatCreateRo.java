package x7.demo.ro;

import x7.demo.entity.TestBoo;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author Sim
 */
public class CatCreateRo {

    private long id;
    private long userId;
    private String type;
    private String name;
    private String taxType;
    private Long dogId;
    private Long test;
    private List<Long> list;
    private TestBoo testBoo;
    private List<String> testList;
    private LocalDate createAt;
    private boolean isDone;

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

    public boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }
}
