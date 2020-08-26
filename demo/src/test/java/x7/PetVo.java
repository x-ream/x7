package x7;

import x7.demo.bean.Cat;

import java.util.List;

public class PetVo  {

    private Cat cat;
    private List<Cat> catList;

    public Cat getCat() {
        return cat;
    }

    public void setCat(Cat cat) {
        this.cat = cat;
    }

    public List<Cat> getCatList() {
        return catList;
    }

    public void setCatList(List<Cat> catList) {
        this.catList = catList;
    }

}
