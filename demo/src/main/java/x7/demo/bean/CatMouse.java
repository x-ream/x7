package x7.demo.bean;

import io.xream.sqli.annotation.X;

public class CatMouse {

    @X.Key
    private long id;
    private long catId;
    private long mouseId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCatId() {
        return catId;
    }

    public void setCatId(long catId) {
        this.catId = catId;
    }

    public long getMouseId() {
        return mouseId;
    }

    public void setMouseId(long mouseId) {
        this.mouseId = mouseId;
    }

    @Override
    public String toString() {
        return "CatMouse{" +
                "id=" + id +
                ", catId=" + catId +
                ", mouseId=" + mouseId +
                '}';
    }
}
