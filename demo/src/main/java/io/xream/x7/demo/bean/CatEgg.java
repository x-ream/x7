package io.xream.x7.demo.bean;

import io.xream.x7.common.repository.X;

public class CatEgg {

    @X.Key
    private long id;
    private String name;
    private long catId;

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

    public long getCatId() {
        return catId;
    }

    public void setCatId(long catId) {
        this.catId = catId;
    }

    @Override
    public String toString() {
        return "CatEgg{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", catId=" + catId +
                '}';
    }
}
