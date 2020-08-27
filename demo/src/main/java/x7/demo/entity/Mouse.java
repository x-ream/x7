package x7.demo.entity;

import io.xream.sqli.annotation.X;

public class Mouse {

    @X.Key
    private String id;
    private String test;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "Mouse{" +
                "id=" + id +
                ", test='" + test + '\'' +
                '}';
    }
}
