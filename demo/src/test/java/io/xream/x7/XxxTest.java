package io.xream.x7;

//import io.seata.spring.annotation.GlobalTransactional;

import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.CriteriaBuilder;
import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.common.web.Direction;
import io.xream.x7.common.web.ViewEntity;
import io.xream.x7.demo.CatRO;
import io.xream.x7.demo.bean.CatTest;
import io.xream.x7.demo.bean.*;
import io.xream.x7.demo.controller.XxxController;
import io.xream.x7.demo.remote.TestServiceRemote;
import io.xream.x7.reyc.api.ReyTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
    private DistributionLockTester distributionLockTester;

    public  void refreshByCondition() {

        controller.refreshByCondition();

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
        distributionLockTester.test(cat);
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


    public ViewEntity testOneKey(){
        return this.testServiceRemote.testOneKey(10L);
    }

    public void testEnum(){
        System.out.println(TestBoo.BOO.getClass());
        System.out.println(TestBoo.BOO.getClass().getSuperclass().toGenericString());
        System.out.println("TRUE = " + TestBoo.BOO.getClass().getSuperclass().isEnum());
        System.out.println("TestBoo.BOO.getView(): " + TestBoo.BOO);
        System.out.println("TestBoo.BOO.name(): " + TestBoo.BOO.name());
    }

    public void testCreate() {

        Cat cat = new Cat();
        cat.setId(251L);
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
}
