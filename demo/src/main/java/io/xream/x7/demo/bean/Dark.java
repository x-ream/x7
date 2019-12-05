package io.xream.x7.demo.bean;

import x7.core.repository.X;

public class Dark {

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
        return "Dark{" +
                "id='" + id + '\'' +
                ", test='" + test + '\'' +
                '}';
    }
}
