package x7;

//import io.seata.spring.annotation.GlobalTransactional;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.xream.sqli.builder.Criteria;
import io.xream.sqli.builder.CriteriaBuilder;
import io.xream.sqli.builder.ReduceType;
import io.xream.sqli.builder.RefreshCondition;
import io.xream.sqli.cache.L2CacheFilter;
import io.xream.sqli.page.Direction;
import io.xream.x7.base.util.JsonX;
import io.xream.x7.base.web.ViewEntity;
import io.xream.x7.fallback.FallbackOnly;
import io.xream.x7.reyc.api.ReyTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import x7.demo.entity.CatTest;
import x7.demo.entity.*;
import x7.demo.controller.CatEggController;
import x7.demo.controller.OrderController;
import x7.demo.controller.XxxController;
import x7.demo.remote.TestServiceRemote;
import x7.demo.ro.CatRO;
import x7.demo.service.DogService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Component
public class XxxTest {

    private Executor executor = Executors.newFixedThreadPool(2);

    @Autowired
    private DogService dogService;

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

    @Autowired
    private CatEggController catEggController;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public  void refreshByCondition() {
        RateLimiterConfig config;

        controller.refreshByCondition();

    }

    public  void testListPlainValue() {

        CatRO cat = new CatRO();

        ViewEntity ve = this.controller.testListPlainValue(cat);

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

    public void testNonPagedCacheGrouped(){

        L2CacheFilter.filter("BL");

        CatRO ro = new CatRO();

        ViewEntity ve = this.controller.nonPaged(ro);

//        System.out.println("\n______Result: " + ve);
    }

    public void testNonPaged(){

        CatRO ro = new CatRO();

        ViewEntity ve = this.controller.nonPaged(ro);

//        System.out.println("\n______Result: " + ve);
    }

    public void create(){
        L2CacheFilter.filter("BL");
        this.controller.create();
    }

    public void distinct(){

        CatRO ro = new CatRO();
        ro.setOrderBy("catTest.id");
        ro.setDirection(Direction.DESC);
        ro.setTotalRowsIgnored(true);
        ViewEntity ve = this.controller.distinct(ro);
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

        CriteriaBuilder builder = CriteriaBuilder.builder(CatTest.class);
        builder.paged().sort("id", Direction.DESC).page(1).rows(10);
        Criteria criteria = builder.build();
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

        L2CacheFilter.filter("BL");

        Cat cat = new Cat();
        cat.setId(471L);
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

    public void removeRefreshCreate(){
        this.controller.removeRefreshCreate();
    }

    public  void testCacheGet(){
        this.controller.testCacheGet();
    }

    public void testCriteriaRemote(){

        CriteriaBuilder builder = CriteriaBuilder.builder(Cat.class);

//		builder.resultKey("id").resultKey("type");
        List<Object> inList = new ArrayList<>();
        inList.add("BL");
        inList.add("NL");
        builder.and().ne("taxType",664);
        builder.and().in("type",inList);
        builder.paged().orderIn("type",inList);

//		Criteria.ResultMapCriteria criteria = builder.build();
        Criteria criteria = builder.build();

        this.testServiceRemote.testCriteriaRemote(criteria);
    }
    
    public void testResultMappedRemote(){

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder.distinct("id").reduce(ReduceType.COUNT,"dogId").groupBy("id");
//        builder.resultKey("id").resultKey("dogId");
        builder.and().eq("type","NL");
        builder.paged().page(1).rows(10).sort("id", Direction.DESC);

        Criteria.ResultMapCriteria ResultMapCriteria = builder.build();

        String json = JsonX.toJson(ResultMapCriteria);
        System.out.println(json);
        System.out.println(ResultMapCriteria.getDistinct());
        ResultMapCriteria = JsonX.toObject(json, Criteria.ResultMapCriteria.class);
        System.out.println(ResultMapCriteria);
        System.out.println(ResultMapCriteria.getDistinct());

        ViewEntity ve = this.testServiceRemote.testResultMappedRemote(ResultMapCriteria);
        System.out.println(ve);
    }

    @FallbackOnly(exceptions = {RuntimeException.class}, fallback = FallbackOnlyTest.class)
    public void testFallbackOnly(String test) {

        System.out.println("testFallbackOnly");

        boolean b = true;
//        if (b){
//            throw new RuntimeException("testFallbackOnly");
//        }
    }


    public void testOrder(){
        Order order1 = new Order();
        order1.setId(5);
        order1.setName("ds0_TEST");
        order1.setType(OrderType.SINGLE);
        order1.setUserId(3);
        this.orderController.create(order1);
    }

    public Order orderSharding(){
        return this.orderController.sharding();
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

    public void testTemporaryTable(){

        this.catEggController.test();

    }

    public void testFindToHandle(){
        this.catEggController.testFindToHanle();
    }

    public void testLock(){
        DogTest dogTest = new DogTest();
        dogTest.setId(3);
//        this.executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                dogService.lock5(dogTest);
//            }
//        });
//        this.executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                dogService.lock5(dogTest);
//            }
//        });
//
//        try{
//            TimeUnit.MILLISECONDS.sleep(1000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        dogService.lock5(dogTest);

        dogService.lock0(dogTest);

    }


    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }

}
