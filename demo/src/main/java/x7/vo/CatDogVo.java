package x7.vo;

import x7.demo.entity.CatTest;
import x7.demo.entity.DogTest;

/**
 * @Author Sim
 */
public class CatDogVo {
    private CatTest c;
    private DogTest d;

    public CatTest getC() {
        return c;
    }

    public void setC(CatTest c) {
        this.c = c;
    }

    public DogTest getD() {
        return d;
    }

    public void setD(DogTest d) {
        this.d = d;
    }

    @Override
    public String toString() {
        return "CatDogVo{" +
                "c=" + c +
                ", d=" + d +
                '}';
    }
}
