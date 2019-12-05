package io.xream.x7;

import io.xream.x7.demo.CatRepository;
import io.xream.x7.demo.bean.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import x7.core.bean.CriteriaBuilder;
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.RefreshCondition;

import java.util.ArrayList;
import java.util.List;


@Service
public class CatTest {

    @Autowired
    private CatRepository repository;
    public void create(){

        Cat cat = new Cat();
        cat.setId(1212);
        cat.setDogId(2);

        this.repository.create(cat);

    }

    public void refresh(){

        Cat cat = new Cat();
        cat.setId(1212);
        cat.setDogId(2222);

//        this.repository.refresh(cat);

    }

    public void refreshByCondition(){

        RefreshCondition refreshCondition = new RefreshCondition();
        refreshCondition.refresh("type","TEST_X");
//        refreshCondition.and().eq("id",1213);
        refreshCondition.and().eq("test",433);

        this.repository.refreshUnSafe(refreshCondition);

    }

    public void remove(){

        this.repository.remove(1212);

    }


    public Cat getOne() {
        Cat cat = new Cat();
        cat.setType("MMM");

        Cat c =  this.repository.getOne(cat);
        System.out.println(c);
        return c;
    }


    public List<Cat> listByCriteria(){
        CriteriaBuilder builder = CriteriaBuilder.build(Cat.class);
        builder.and().eq("type","MMM");

        List<Cat> list = this.repository.list(builder.get());

        System.out.println(list);

        return list;
    }


    public List<Cat> in(){
        List<Long> inList = new ArrayList<>();
        inList.add(3L);
        InCondition inCondition = new InCondition("dogId", inList);
        List<Cat> list = this.repository.in(inCondition);
        System.out.println(list);
        return list;
    }

}
