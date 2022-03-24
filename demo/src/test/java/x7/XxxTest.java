package x7;

//import io.seata.spring.annotation.GlobalTransactional;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.xream.internal.util.JsonX;
import io.xream.rey.api.ReyTemplate;
import io.xream.sqli.api.CacheFilter;
import io.xream.sqli.builder.*;
import io.xream.x7.base.web.ViewEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import x7.demo.controller.CatEggController;
import x7.demo.controller.OrderController;
import x7.demo.controller.XxxController;
import x7.demo.entity.*;
import x7.demo.remote.OrderRemote;
import x7.demo.remote.TestServiceRemote;
import x7.demo.ro.CatCreateRo;
import x7.demo.ro.CatFindRo;
import x7.demo.service.DogService;

import javax.annotation.Resource;
import java.time.LocalDate;
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
    private CatEggController catEggController;
    @Resource(name = "x7.demo.remote.OrderRemote")
    private OrderRemote orderRemote;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public long createId(){
        return this.controller.createId();
    }


    public  void refreshByCondition() {
        RateLimiterConfig config;

        controller.refreshByCondition();

    }

    public  void testListPlainValue() {

        CatFindRo cat = new CatFindRo();

        ViewEntity ve = this.controller.testListPlainValue(cat);

        System.out.println("\n______Result: " + ve);

    }

    public  void testAlia() {
       this.controller.testAlia(new CatFindRo());
    }

    public  void testOne() {

        CatFindRo cat = new CatFindRo();



        ViewEntity ve = this.controller.testOne(cat);

        System.out.println("\n______Result: " + ve);

    }

    public void testNonPagedCacheGrouped(){

        CacheFilter.filter("BL");

        CatFindRo ro = new CatFindRo();

        ViewEntity ve = this.controller.nonPaged(ro);

//        System.out.println("\n______Result: " + ve);
    }

    public void testNonPaged(){

        CatFindRo ro = new CatFindRo();

        ViewEntity ve = this.controller.nonPaged(ro);

//        System.out.println("\n______Result: " + ve);
    }

    public void create(){
        CacheFilter.filter("BL");
        this.controller.create();
    }

    public void resultKeyFuntion(){

        CatFindRo ro = new CatFindRo();
        ro.setOrderBy("catTest.id");
        ro.setDirection(Direction.DESC);
        ro.setTotalRowsIgnored(true);
        ro.setRows(10);
        ro.setPage(2);
        ro.setLast(10);
        ViewEntity ve = this.controller.resultKeyFuntion(ro);
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

        CatFindRo catFindRo = new CatFindRo();
        catFindRo.setCatFriendName("DOG");
        List<Cat> list = testServiceRemote.testFallBack(catFindRo);

//        testServiceRemote.test(new CatRO(),null);

    }


    public void testTime(){

        boolean flag = testServiceRemote.testTimeJack(null);

    }

    public ViewEntity get(){
        return this.controller.get();
    }

    public int getBase(){
        return testServiceRemote.getBase();
    }


    public ViewEntity testCriteria(){

        CriteriaBuilder builder = CriteriaBuilder.builder(CatTest.class);
        builder.nonNull("type");
        builder.or().beginSub().gt("dogId",1).and().nonNull("catFriendName").endSub();
        builder.sort("id", Direction.DESC).paged().ignoreTotalRows().page(1).rows(10);
        Criteria criteria = builder.build();
        controller.testCriteria(criteria);

//        CriteriaBuilder builder1 = CriteriaBuilder.builder(CatTest.class);
//        builder1.nonNull("type");
//        builder1.beginSub().nonNull("catFriendName").or().gt("dogId",1).endSub();
//        builder1.sort("id", Direction.DESC).paged().ignoreTotalRows().page(1).rows(10);
//        Criteria criteria1 = builder1.build();
//        controller.testCriteria(criteria1);
        return ViewEntity.ok();
    }

    public ViewEntity testResultMapSimpleSource(){

        ViewEntity ve =  controller.testResultMapSimpleSource();
        System.out.println(ve);
        return ve;
    }


    public ViewEntity testRefreshConditionRemote(){

        return testServiceRemote.testRefreshConditionnRemote(
                RefreshBuilder.builder().refresh("createAt",new Date()).eq("id",100).build()
        );
    }



    public ViewEntity testListWithEnum(){
        ViewEntity ve = this.controller.listWithEnum();
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



    public void testCreate() throws Exception {

        CacheFilter.filter("BL");

        CatCreateRo cat = new CatCreateRo();
        cat.setId(629);
//        cat.setTest(542223L);
        cat.setType("NL");
        cat.setTestBoo(TestBoo.TEST);
        cat.setCreateAt(LocalDate.now());
        cat.setIsDone(false);

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

//		Criteria.ResultMapCriteria criteria = builder.build();
        Criteria criteria = builder.build();

        this.testServiceRemote.testCriteriaRemote(criteria);
    }
    
    public void testResultMappedRemote(){

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder.distinct("id").reduce(ReduceType.COUNT,"dogId").groupBy("id");
//        builder.resultKey("id").resultKey("dogId");
        builder.and().eq("type","NL");
        builder.sort("id", Direction.DESC).paged().page(1).rows(10);

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

    public ViewEntity listCat(){
        return this.controller.listCat();
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
        dogTest.setUserName("XXXX");
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

        dogService.lock5("xxxx","zzzz");

    }

    public void testSimple(){
        this.controller.testSimple();
    }

    public void testOrderRemote(){
        this.orderRemote.verify();
    }

    public void testInCondtion(){
        this.controller.testInCondition();
    }

}
