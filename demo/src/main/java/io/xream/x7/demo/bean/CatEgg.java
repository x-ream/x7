package io.xream.x7.demo.bean;

import io.xream.sqli.annotation.X;

public class CatEgg {

    @X.Key
    private long id;
    private String name;
    private long dogId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDogId() {
        return dogId;
    }

    public void setDogId(long dogId) {
        this.dogId = dogId;
    }

    @Override
    public String toString() {
        return "CatEgg{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dogId=" + dogId +
                '}';
    }
}
