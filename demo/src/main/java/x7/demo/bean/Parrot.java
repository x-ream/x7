package x7.demo.bean;

import io.xream.sqli.annotation.X;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Parrot implements Serializable{

    private static final long serialVersionUID = 5708147778967785698L;

    @X.Key
    private Long id;
    @X.Mapping("cat_type")
    private String type;
    private String taxType;
    private List<Long> list;
    private Date createAt;
    private TestBoo testBoo;
    private List<String> testList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Long> getList() {
        return list;
    }

    public void setList(List<Long> list) {

        this.list = list;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
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

    @Override
    public String toString() {
        return "Cat{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", taxType='" + taxType + '\'' +
                ", list=" + list +
                ", createAt=" + createAt +
                ", testBoo=" + testBoo +
                ", testList=" + testList +
                '}';
    }
}
