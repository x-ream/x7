package x7;

import io.xream.internal.util.JsonX;
import io.xream.sqli.builder.*;
import io.xream.sqli.page.Page;
import io.xream.sqli.util.SqliJsonUtil;
import io.xream.x7.base.web.ViewEntity;
import x7.demo.entity.Cat;
import x7.demo.entity.TestBoo;

import java.time.LocalDate;
import java.util.*;

/**
 * @author Sim
 */
public class JsonUtilTester {


    public static void main(String[] args) {

        Map<String,String> mapTest = new HashMap<>();
        mapTest.put("JOO","nana");

        Cat cat = new Cat();
        cat.setId(10L);
        cat.setDogId(11L);
        cat.setCreateAt(LocalDate.now());
        cat.setTestBoo(TestBoo.BOO);
        cat.setTaxType("XXXX");
        cat.setTestList(Arrays.asList("gr53","3342"));

        Cat cat1 = new Cat();
        cat1.setId(11L);
        cat1.setDogId(11L);
        cat1.setCreateAt(LocalDate.now());
        cat1.setTestBoo(TestBoo.TEST);
        cat1.setTaxType("XXXX");
        cat1.setTestList(Arrays.asList("gr53ED","334223"));

        String str = SqliJsonUtil.toJson(cat);
        System.out.println("toJackson: " + str);
        System.out.println("toFastjson: " + JsonX.toJson(cat));
        cat = SqliJsonUtil.toObject(str, Cat.class);
        System.out.println("-------->" + SqliJsonUtil.toObject(str,SmallCat.class));
        SmallCat smallCat = SqliJsonUtil.toObject(str,SmallCat.class);
        System.out.println("-------->" + SqliJsonUtil.toObject(SqliJsonUtil.toJson(smallCat),Cat.class));
        System.out.println("-------->" + JsonX.toObject(SqliJsonUtil.toJson(smallCat),Cat.class));


        Map map = SqliJsonUtil.toMap(str);
        System.out.println(map.get("createAt"));

        List<Cat> list = new ArrayList<>();
        list.add(cat);
        list.add(cat1);

        String listStr = SqliJsonUtil.toJson(list);
        List<Cat> catList = SqliJsonUtil.toList(listStr,Cat.class);
        System.out.println("_________catList: " + catList);


        ViewEntity ve = ViewEntity.ok(mapTest);
        str = SqliJsonUtil.toJson(ve);
        System.out.println(str);

        ve = SqliJsonUtil.toObject(str,ViewEntity.class);
        System.out.println(ve);
//        Object o = SqliJsonUtil.toObject(ve.getBody());
        System.out.println("______map.J" + ve.getBody());
        Map mm = JsonX.toMap(ve.getBody());
        System.out.println("______map.F" + mm);

        Page<Cat> page = new Page<>();
        page.setClzz(Cat.class);
        page.reSetList(new ArrayList<>());

        String pageStr = SqliJsonUtil.toJson(page);
        String ps = JsonX.toJson(page);
        Page p = SqliJsonUtil.toObject(pageStr,Page.class);
        Page pp = SqliJsonUtil.toObject(ps,Page.class);

        InCondition inCondition = InCondition.of("testBoo",Arrays.asList(TestBoo.BOO,TestBoo.TEST));
        System.out.println(inCondition);
        String enumStr = SqliJsonUtil.toJson(inCondition);
        inCondition = SqliJsonUtil.toObject(enumStr,InCondition.class);
        System.out.println(inCondition);


        RefreshCondition refreshCondition = RefreshBuilder.builder()
                .refresh("testBoo", TestBoo.BOO)
                .refresh("testList", Arrays.asList("ZZZZZ","xxxx"))
                .refresh("test = test - 3")
                .refresh("createAt", System.currentTimeMillis())
                .gt("createAt", 0)
                .in("id", Arrays.asList(247, 248,512)).build();
        String fastStr = JsonX.toJson(refreshCondition);
        String jackStr = SqliJsonUtil.toJson(refreshCondition);
        System.out.println("__fastStr: " + fastStr);
        System.out.println("__jackStr: " + jackStr);
        RefreshCondition r1 = JsonX.toObject(fastStr,RefreshCondition.class);
        RefreshCondition r2 = SqliJsonUtil.toObject(jackStr,RefreshCondition.class);

        Criteria criteria = new Criteria();
        criteria.setClzz(Cat.class);
        criteria.getBbList().add(new Bb());
        fastStr = JsonX.toJson(criteria);
        jackStr = SqliJsonUtil.toJson(criteria);
        System.out.println("__fastStr: " + fastStr);
        System.out.println("__jackStr: " + jackStr);

        
    }

}
