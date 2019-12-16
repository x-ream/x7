package io.xream.x7;

import io.xream.x7.demo.CatRO;
import io.xream.x7.demo.bean.Cat;
import io.xream.x7.demo.bean.CatTest;
import io.xream.x7.demo.bean.DogTest;
import io.xream.x7.demo.controller.XxxController;
import io.xream.x7.demo.remote.TestServiceRemote;
import io.xream.x7.reyc.api.ReyTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import x7.core.bean.Criteria;
import x7.core.bean.CriteriaBuilder;
import x7.core.bean.condition.RefreshCondition;
import x7.core.web.Direction;
import x7.core.web.ViewEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Component
public class XxxTest {

    @Autowired
    private ReyTemplate reyTemplate;

    @Autowired
    private TestServiceRemote testServiceRemote;
    @Autowired
    private XxxController controller;

    public  void refreshByCondition() {

        Cat cat = new Cat();

        cat.setDogId(2323);
        cat.setId(4);

        ViewEntity ve = controller.refreshByCondition(cat);

//        Executor executor = Executors.newFixedThreadPool(3);
//
//        for (int i=0; i<3; i++) {
//
//            executor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    ViewEntity ve = testServiceRemote.refreshByCondition(cat);
//                    System.out.println("--------------- "+ve);
//                }
//            });
//
//        }
//
//        try {
//            Thread.sleep(10000000);
//        }catch (Exception e){
//
//        }
    }

    public  void test() {

        CatRO cat = new CatRO();

        ViewEntity ve = this.controller.test(cat);

        System.out.println("\n______Result: " + ve);

    }

    public  void testAlia() {

        CatRO cat = new CatRO();

        ViewEntity ve = this.controller.testAlia(cat);

        System.out.println("\n______Result: " + ve);

    }

    public  void testOne() {

        CatRO cat = new CatRO();
        cat.setRows(10);
        cat.setPage(1);



        ViewEntity ve = this.controller.testOne(cat);

        System.out.println("\n______Result: " + ve);

    }

    public void testNonPaged(){

        CatRO ro = new CatRO();

        ViewEntity ve = this.controller.nonPaged(ro);

        System.out.println("\n______Result: " + ve);
    }

    public void create(){
        this.controller.create();
    }

    public void domain(){
        this.controller.domain();
    }

    public void distinct(){

        ViewEntity ve = this.controller.distinct(null);
        System.out.println(ve);
    }

    public void testReyClient(){

//        testServiceRemote.test(new CatRO(), new Url() {
//            @Override
//            public String value() {
//                return "127.0.0.1:8868/xxx/reyc/test";
//            }
//        });

        List<Cat> list = testServiceRemote.testFallBack(new CatRO());

//        testServiceRemote.test(new CatRO(),null);

    }


    public void testTime(){

        boolean flag = testServiceRemote.testTimeJack();

    }

    public int getBase(){

        return testServiceRemote.getBase();
    }


    public ViewEntity testCriteria(){

        CriteriaBuilder builder = CriteriaBuilder.build(CatTest.class);
        builder.paged().sort("id", Direction.DESC).page(1).rows(10);
        Criteria criteria = builder.get();
        return testServiceRemote.testCriteria(criteria);
    }

    public ViewEntity testResultMapped(){

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped(CatTest.class);
        builder.paged().sort("catTest.id", Direction.DESC).page(1).rows(10);
        Criteria.ResultMappedCriteria criteria = builder.get();
        return testServiceRemote.testResultMap(criteria);
    }

    public ViewEntity testDomain(){

        CriteriaBuilder.DomainObjectBuilder builder = CriteriaBuilder.buildDomainObject(CatTest.class, DogTest.class);
//        builder.paged().sort("catTest.id", Direction.DESC).page(1).rows(10);
        Criteria.DomainObjectCriteria criteria = builder.get();
        return testServiceRemote.testDomain(criteria);
    }

    public ViewEntity testRefreshCondition(){
        RefreshCondition<CatTest> refreshCondition = new RefreshCondition<>();
//        refreshCondition.and().eq("id",0);
        refreshCondition.refresh("isCat",true).and().eq("id",5);


//        String str =this.reyTemplate.support(null, false,
//                new BackendService() {
//                    @Override
//                    public String handle() {
//                        return HttpClientUtil.post("http://127.0.0.1:8868/xxx/refreshCondition/test",refreshCondition);
//                    }
//
//                    @Override
//                    public Object fallback() {
//                        System.out.println("FALL BACK TEST");
//                        return null;
//                    }
//                });
//
//        return ViewEntity.ok(str);
        return testServiceRemote.testRefreshConditionn(refreshCondition);
    }



    public ViewEntity testListCriteria(){
        return this.controller.listCriteria();
    }

}
