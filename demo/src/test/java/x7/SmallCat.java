package x7;

import x7.demo.entity.TestBoo;

import java.util.Date;

/**
 * @Author Sim
 */
public class SmallCat {
    private Date createAt;
    private TestBoo testBoo;

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

    @Override
    public String toString() {
        return "SmallCat{" +
                "createAt=" + createAt +
                ", testBoo=" + testBoo +
                '}';
    }
}
