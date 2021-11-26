package x7.demo.test;

import x7.demo.entity.Cat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Sim
 */
public class ListTester {

    public static void main(String[] args) {
        List<Cat> list = new ArrayList<>();
        Cat cat = new Cat();
        cat.setId(1);
        cat.setDogId(1);
        Cat cat1 = new Cat();
        cat1.setId(2);
        cat1.setDogId(1);
        list.add(cat);
        list.add(cat1);
        list = list.stream().filter(c -> c.getDogId() == 0).collect(Collectors.toList());
        System.out.println(list);
    }
}
