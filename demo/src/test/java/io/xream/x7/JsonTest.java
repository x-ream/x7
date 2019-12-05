package io.xream.x7;

import io.xream.x7.demo.bean.Cat;
import x7.core.util.JsonX;

public class JsonTest {

    public static void main(String[] args) {
        Cat catTest = new Cat();
        String str = JsonX.toJson(catTest);
        System.out.println(str);
    }
}
