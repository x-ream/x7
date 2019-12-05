package io.xream.x7.demo.bean;

import x7.core.bean.Transformed;
import x7.core.repository.X;

public class TransformedDemo implements Transformed {

    @X.Key
    private String id;
    private String alia;
    private String value;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlia() {
        return alia;
    }

    public void setAlia(String alia) {
        this.alia = alia;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TransformedDemo{" +
                "id='" + id + '\'' +
                ", alia='" + alia + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
