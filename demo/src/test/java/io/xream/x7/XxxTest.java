package io.xream.x7;

//import io.seata.spring.annotation.GlobalTransactional;

import io.xream.x7.common.async.CasualWorker;
import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.CriteriaBuilder;
import io.xream.x7.common.bean.ReduceType;
import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.common.util.JsonX;
import io.xream.x7.common.web.Direction;
import io.xream.x7.common.web.ViewEntity;
import io.xream.x7.demo.ro.CatRO;
import io.xream.x7.demo.bean.Cat;
import io.xream.x7.demo.bean.CatTest;
import io.xream.x7.demo.bean.Order;
import io.xream.x7.demo.bean.TestBoo;
import io.xream.x7.demo.controller.OrderController;
import io.xream.x7.demo.controller.XxxController;
import io.xream.x7.demo.remote.TestServiceRemote;
import io.xream.x7.fallback.FallbackOnly;
import io.xream.x7.reyc.api.ReyTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class XxxTest {

    @Autowired
    private ReyTemplate reyTemplate;

    @Autowired
    private TestServiceRemote testServiceRemote;
    @Autowired
    private XxxController controller;
    @Autowired
    private OrderController orderController;

    @Autowired
    private DistributionLockTester distributionLockTester;


    public  void refreshByCondition() {

        controller.refreshByCondition();

    }

    public  void testFindByResultMapped() {

        CatRO cat = new CatRO();

        ViewEntity ve = this.controller.testFindByResultMapped(cat);

        System.out.println("\n______Result: " + ve);

    }

    public  void testAlia() {

        CatRO cat = new CatRO();

        ViewEntity ve = this.controller.testAlia(cat);

        System.out.println("\n______Result: " + ve);

    }

    public  void testOne() {

        CatRO cat = new CatRO();



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

    public void distinct(){

        ViewEntity ve = this.controller.distinct(null);
        System.out.println(ve);
    }

//    @GlobalTransactional
    public void testReyClient(){

//        testServiceRemote.test(new CatRO(), new Url() {
//            @Override
//            public String value() {
//                return "127.0.0.1:8868/xxx/reyc/test";
//            }
//        });

        CatRO catRO = new CatRO();
        catRO.setCatFriendName("DOG");
        List<Cat> list = testServiceRemote.testFallBack(catRO);

//        testServiceRemote.test(new CatRO(),null);

    }


    public void testTime(){

        boolean flag = testServiceRemote.testTimeJack();

    }

    public ViewEntity get(){
        ViewEntity ve = this.controller.get();
        System.out.println(ve.getBody());
        return ve;
    }

    public int getBase(){

        return testServiceRemote.getBase();
    }


    public ViewEntity testCriteria(){

        CriteriaBuilder builder = CriteriaBuilder.build(CatTest.class);
        builder.paged().sort("id", Direction.DESC).page(1).rows(10);
        Criteria criteria = builder.get();
        return controller.testCriteria(criteria);
    }

    public ViewEntity testResultMapped(){

        ViewEntity ve =  controller.testResultMap();
        System.out.println(ve);
        return ve;
    }


    public ViewEntity testRefreshConditionRemote(){

        return testServiceRemote.testRefreshConditionnRemote(
                RefreshCondition.build().refresh("createAt",new Date()).eq("id",100)
        );
    }



    public ViewEntity testListCriteria(){
        ViewEntity ve = this.controller.listCriteria();
        return ve;
    }


    public ViewEntity testRestTemplate(){

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60000);
        requestFactory.setReadTimeout(60000);

        String url = "http://127.0.0.1:8868/xxx/test/rest";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("TX_XID", "eg564ssasdd");

        CatTest cat = new CatTest();
        cat.setType("TEST_CAT");
        HttpEntity<CatTest> requestEntity = new HttpEntity<CatTest>(cat,httpHeaders);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        ResponseEntity<String> result = restTemplate.postForEntity(url, requestEntity,String.class);

        return ViewEntity.ok(result);
    }

    public void testLock(){
        Cat cat = new Cat();
        cat.setId(1000L);
        cat.setType("LOCK--------sss");
        Criteria.X x = new Criteria.X();
//        cat.getListX().add(x);
        CasualWorker.accept(new Runnable() {
            @Override
            public void run() {
                distributionLockTester.test("_test_cat_");
            }
        });
        distributionLockTester.test("_test_cat_");
    }


    public void testList(){
        this.controller.list();
    }

    public ViewEntity testRemove(){
        return this.controller.remove();
    }

    public ViewEntity createBatch() {
        return this.controller.createBatch();
    }

    public ViewEntity in(){
        return this.controller.in();
    }

    public ViewEntity list(){
        return this.controller.list();
    }


    public ViewEntity testOneKeyRemote(){
        return this.testServiceRemote.testOneKey(10L);
    }



    public void testCreate() {

        Cat cat = new Cat();
        cat.setId(451L);
        cat.setTest(255442L);
        cat.setType("NL");
        cat.setTestBoo(TestBoo.BOO);

        this.controller.createCat(cat);
    }

    public void testCreateOrReplace(){

        Cat cat = new Cat();
        cat.setId(251L);
//        cat.setTest(255442L);
        cat.setType("BL");
        cat.setTestBoo(TestBoo.TEST);

        this.controller.refreshOrCreat(cat);
    }

    public void removeOrRefreshOrCreate(){
        this.controller.removeOrRefreshOrCreate();
    }

    public  void testCacheGet(){
        this.controller.testCacheGet();
    }

    public void testCriteriaRemote(){

        CriteriaBuilder builder = CriteriaBuilder.build(Cat.class);

//		builder.resultKey("id").resultKey("type");
        List<Object> inList = new ArrayList<>();
        inList.add("BL");
        inList.add("NL");
        builder.and().ne("taxType",664);
        builder.and().in("type",inList);
        builder.paged().orderIn("type",inList);

//		Criteria.ResultMappedCriteria criteria = builder.get();
        Criteria criteria = builder.get();

        this.testServiceRemote.testCriteriaRemote(criteria);
    }
    
    public void testResultMappedRemote(){

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.distinct("id").reduce(ReduceType.COUNT,"dogId").groupBy("id");
//        builder.resultKey("id").resultKey("dogId");
        builder.and().eq("type","NL");
        builder.paged().page(1).rows(10).sort("id",Direction.DESC);

        Criteria.ResultMappedCriteria resultMappedCriteria = builder.get();

        String json = JsonX.toJson(resultMappedCriteria);
        System.out.println(json);
        System.out.println(resultMappedCriteria.getDistinct());
        resultMappedCriteria = JsonX.toObject(json, Criteria.ResultMappedCriteria.class);
        System.out.println(resultMappedCriteria);
        System.out.println(resultMappedCriteria.getDistinct());

        this.testServiceRemote.testResultMappedRemote(resultMappedCriteria);

    }

    @FallbackOnly(exceptions = {RuntimeException.class}, fallback = FallbackOnlyTest.class)
    public void testFallbackOnly(String test) {

        System.out.println("testFallbackOnly");

        boolean b = true;
        if (b){
            throw new RuntimeException("testFallbackOnly");
        }
    }


    public void testOrder(){
        Order order1 = new Order();
        order1.setId(4);
        order1.setName("ds0_TEST");
        this.orderController.create(order1);
    }

    public void testOrderFind(){
        this.orderController.find();
    }

    public void testOrderFindByAlia(){
        this.orderController.findBuAlia();
    }

    public void inOrder(){
        this.orderController.in();
    }

}
