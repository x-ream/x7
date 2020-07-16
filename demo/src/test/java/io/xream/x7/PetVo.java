package io.xream.x7;

import io.xream.x7.demo.bean.Cat;
import io.xream.x7.common.web.IdView;

import java.util.List;
import java.util.Map;

public class PetVo implements IdView {

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


    @Override
    public void transform(Map<Object, Object> viewMap) {

    }
}
