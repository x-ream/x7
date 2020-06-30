package io.xream.x7.demo.controller;


import io.xream.x7.common.bean.*;
import io.xream.x7.common.bean.condition.InCondition;
import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.common.bean.condition.RemoveRefreshCreate;
import io.xream.x7.common.cache.CacheableL3;
import io.xream.x7.common.util.JsonX;
import io.xream.x7.common.web.Page;
import io.xream.x7.common.web.ViewEntity;
import io.xream.x7.demo.*;
import io.xream.x7.demo.bean.*;
import io.xream.x7.demo.ro.CatRO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.xream.x7.common.bean.JoinType.INNER_JOIN;
import static io.xream.x7.common.bean.Op.GT;
import static io.xream.x7.common.bean.ReduceType.COUNT_DISTINCT;
import static io.xream.x7.common.bean.ReduceType.SUM;
import static io.xream.x7.common.web.Direction.DESC;


@RestController
@RequestMapping("/xxx")
//@Transactional
public class XxxController {

    @Autowired
    private CatTestRepository repository;// sample

    @Autowired
    private CatRepository catRepository;// sample

    @Autowired
    private PigRepository pigRepository;

    @Autowired
    private TimeJackRepository timeJackRepository;

    @RequestMapping("/test/rest")
    public ViewEntity testRest(@RequestBody CatTest catTest, HttpServletRequest request) {

        String xid = request.getHeader("TX_XID");

        return ViewEntity.ok("test_rest_ok_" + xid);
    }

    @RequestMapping("/get")
//	@Transactional
    public ViewEntity get() {
//		Cat cat = this.catRepository.get(100);
        Cat cat = new Cat();
        cat.setId(10L);
        cat = this.catRepository.getOne(cat);
        System.out.println(cat);
        return ViewEntity.ok(cat);
    }

    @RequestMapping("/create/cat")
//	@Transactional
    public ViewEntity createCat(@RequestBody Cat cat) {
        this.catRepository.create(cat);
        return ViewEntity.ok();
    }

    @RequestMapping("/createOrReplace")
//	@Transactional
    public ViewEntity refreshOrCreat(@RequestBody Cat cat) {
        boolean flag = this.catRepository.createOrReplace(cat);
        return ViewEntity.ok(flag);
    }

    @RequestMapping("/create")
    @Transactional
    public ViewEntity create() {

        Cat cat = new Cat();
        cat.setId(352L);
        cat.setDogId(2);
        cat.setCreateAt(new Date());
        cat.setTestBoo(TestBoo.TEST);
        cat.setList(Arrays.asList(6L, 8L));
        cat.setTestList(Arrays.asList("BIG CAT", "small cat"));

        this.catRepository.create(cat);

        cat = new Cat();
        cat.setId(353L);
        cat.setDogId(2);
        cat.setCreateAt(new Date());
        cat.setTestBoo(TestBoo.TEST);
        cat.setList(Arrays.asList(1L, 2L));
        cat.setTestList(Arrays.asList("THR CAT", "moo cat"));

        this.catRepository.create(cat);
        throw new RuntimeException("-----------------------------> test wawawawa");

//		return ViewEntity.ok();
    }

    @RequestMapping("/refreshByCondition")
    public ViewEntity refreshByCondition() {

        Dark dark = new Dark();
        dark.setTest("REFRESHED");
        dark.setId("666");

        boolean flag = this.catRepository.refresh(
                RefreshCondition.build()
                        .refresh("testBoo", "TEST")
                        .refresh("testList", Arrays.asList("8989","2222"))
                        .refresh("testObj", dark)
                        .refresh("test = test - 3")
                        .refresh("createAt", System.currentTimeMillis())
                        .lt("createAt", 0)
                        .in("id", Arrays.asList(247, 248))
//                        .sourceScript("cat LEFT JOIN dogTest on dogTest.id = cat.dogId")
        );//必须带ID更新，没ID报错
//		this.catRepository.refreshUnSafe(refreshCondition);//可以多条更新

        throw new RuntimeException("TEST ORDER");

//        return ViewEntity.ok(flag);
    }


    @RequestMapping("/distinct")
    public ViewEntity distinct(@RequestBody CatRO ro) {

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped(ro);
        builder.distinct("catTest.dogId")
                .distinct("catTest.catFriendName")
                .reduce(COUNT_DISTINCT, "catTest.id")
                .reduce(SUM, "dogTest.petId", Having.of(GT, 1)).groupBy("catTest.xxx")
                .resultKeyFunction(ResultKeyAlia.of("catTest","xxx"),"YEAR(?)","catTest.time")
//                .paged().ignoreTotalRows().page(1).rows(2).sort("catTest.dogId", DESC);
                .paged(ro);
        String sourceScript = "FROM catTest INNER JOIN dogTest ON catTest.dogId = dogTest.id";
        Criteria.ResultMappedCriteria resultMapped = builder.get();
        resultMapped.setSourceScript(sourceScript);
        Page<Map<String, Object>> page = repository.find(resultMapped);

        return ViewEntity.ok(page);
    }


    //	@CacheableL3(expireTime = 3, timeUnit = TimeUnit.MINUTES)
    @RequestMapping("/listPlainValue")
    public ViewEntity testListPlainValue(@RequestBody CatRO ro) {

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.distinct("catTest.id");
        builder.beginSub().gte("dogTest.id", 1000).endSub();
//		builder.and().in("catTest.catFriendName", inList);
//		builder.paged().ignoreTotalRows().orderIn("catTest.catFriendName",inList);//按IN查询条件排序，有值，就过滤掉orderBy

        builder.sourceScript("FROM catTest INNER JOIN dogTest ON dogTest.id = catTest.dogId");
        //或者如下
        builder.sourceScript().source("catTest");
        builder.sourceScript().source("dogTest").joinType(INNER_JOIN).on("id", JoinFrom.of("catTest", "dogId"));

        Criteria.ResultMappedCriteria resultMapped = builder.get();

        List<Long> idList = repository.listPlainValue(Long.class, resultMapped);


        return ViewEntity.ok(idList);

    }


    @RequestMapping("/testAlia")
    @CacheableL3(expireTime = 2, timeUnit = TimeUnit.MINUTES)
    public ViewEntity testAlia(@RequestBody CatRO ro) {

        List<Object> inList = new ArrayList<>();
        inList.add("gggg");
        inList.add("xxxxx");


//		ro.setSortList(sortList);

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.distinct("c.dogId").reduce(ReduceType.GROUP_CONCAT_DISTINCT, "c.type").groupBy("c.dogId");
        builder.or().in("c.catFriendName", inList);
        builder.or().eq("d.petId", 0);
        builder.or().lt("c.time",System.currentTimeMillis());
        builder.and().in("c.dogId", Arrays.asList(0));
        builder.paged().orderIn("c.catFriendName", inList).sort("c.id", DESC);
        builder.sourceScript("catTest c LEFT JOIN dogTest d on c.dogId = d.id");
        Criteria.ResultMappedCriteria resultMapped = builder.get();
        Page<Map<String, Object>> page = repository.find(resultMapped);

        return ViewEntity.ok(page);

    }


    @RequestMapping("/testOne")
    public ViewEntity testOne(@RequestBody CatRO ro) {


        String[] resultKeys = {
                "c.id",
                "c.type"
        };
//		ro.setOrderBy("cat.dogId");

        ro.setResultKeys(resultKeys);

        List<Object> inList = new ArrayList<>();
        inList.add("WHITE");
        inList.add("BLACK");

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.distinct("c.id").reduce(COUNT_DISTINCT, "c.dogId").groupBy("c.id");
        builder.and().nin("c.type", Arrays.asList("WHITE", "BLACK"));
        builder.paged().orderIn("c.type", Arrays.asList("WHITE", "BLACK"));
        builder.sourceScript().source("catTest").alia("c");

        Criteria.ResultMappedCriteria resultMapped = builder.get();

        Page<Map<String, Object>> pagination = repository.find(resultMapped);


        return ViewEntity.ok(pagination);

    }


    public ViewEntity nonPaged(@RequestBody CatRO ro) {

//		CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped(Cat.class);
        CriteriaBuilder builder = CriteriaBuilder.build(Cat.class);

//		builder.resultKey("id").resultKey("type");

        builder.and().eq("taxType", null);
        builder.and().in("type", Arrays.asList("BL","NL"));
        builder.paged().ignoreTotalRows().orderIn("type", Arrays.asList("BL","NL"));

//		Criteria.ResultMappedCriteria criteria = builder.get();
        Criteria criteria = builder.get();
        Page p = catRepository.find(criteria);

        return ViewEntity.ok(p);
    }

    @RequestMapping(value = "/reyc/test")
    public List<Cat> testRecClient() {

//		try {
//			TimeUnit.HOURS.sleep(1);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
        List<Cat> list = new ArrayList<>();
        Cat cat = new Cat();
        cat.setTest(100);
        list.add(cat);
        return list;
    }


    @CrossOrigin(origins = "http://xxxxx.com")
    @RequestMapping(value = "/time/test", method = RequestMethod.GET)
    public ViewEntity testTime() {

        Date date = new Date();
        System.out.println(date);

        TimeJack tj = new TimeJack();

        tj.setId(100);
        tj.setName("XXXXXX");
        tj.setDate(new Date());

        this.timeJackRepository.create(tj);

        List<TimeJack> list = this.timeJackRepository.list();

        return ViewEntity.ok(tj);
    }


    @RequestMapping(value = "/pig/get/{id}", method = RequestMethod.GET)
    public Pig getPig(@PathVariable long id) {

        Pig pig = this.pigRepository.get(id);

        System.out.println(pig);
        return pig;
    }

    @RequestMapping("/reyc/base")
    public int getBase() {
//		return "x7-demo/xxx/reyc/str";
        return 10;
    }

    @RequestMapping("/remote/resultmapped/test")
    ViewEntity testResultMappedRemote(@RequestBody Criteria.ResultMappedCriteria criteria) {

        System.out.println(criteria);
        Page<Map<String, Object>> page = this.catRepository.find(criteria);

        return ViewEntity.ok(page);
    }

    @RequestMapping("/resultmap/test")
    public ViewEntity testResultMap() {

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder
                .distinct("id")
                .reduce(COUNT_DISTINCT, "dogId")
                .groupBy("id")
        ;
        builder.and().eq("type", "NL");
        builder.paged().ignoreTotalRows().page(1).rows(10).sort("id", DESC);

        Criteria.ResultMappedCriteria resultMappedCriteria = builder.get();
        Page<Map<String, Object>> page = this.catRepository.find(resultMappedCriteria);

        return ViewEntity.ok(page);
    }


    @Transactional
    @RequestMapping("/remote/refreshCondition/test")
    public ViewEntity testRefreshConditionnRemote(@RequestBody RefreshCondition<Cat> refreshCondition) {
        this.catRepository.refresh(refreshCondition);

//        return ViewEntity.ok(refreshCondition);
        throw new RuntimeException("TEST AOP");
    }

    @RequestMapping("/listCriteria")
    public ViewEntity listCriteria() {
        //		CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped(Cat.class);
        CriteriaBuilder builder = CriteriaBuilder.build(Cat.class);

//		builder.resultKey("id").resultKey("type");
        builder
                .in("testBoo", Arrays.asList("BOO"))
                .ne("type",null)
                .eq("dogId",1)
                .or().in("id", Arrays.asList(247, 248));
//		builder.paged().orderIn("type",inList);
        builder.forceIndex("IDX_CAT_DOG_ID");
        builder.paged().ignoreTotalRows().orderIn("testBoo",Arrays.asList("BOO"));


//		Criteria.ResultMappedCriteria criteria = builder.get();
        Criteria criteria = builder.get();

        String str = JsonX.toJson(criteria);
        criteria = JsonX.toObject(str, Criteria.class);
        System.out.println(criteria);

        catRepository.find(criteria);

        return ViewEntity.ok(null);
    }

    @RequestMapping("/list")
    public ViewEntity list() {
        this.catRepository.list();
        return ViewEntity.ok();
    }

    @RequestMapping("/remove")
    public ViewEntity remove() {

        boolean flag = this.catRepository.remove(251);
        return ViewEntity.ok(flag);
    }

    @RequestMapping("/createBatch")
//	@Transactional
    public ViewEntity createBatch() {

        Cat cat = new Cat();
        cat.setId(256L);
        cat.setDogId(2);
        cat.setCreateAt(new Date());
        cat.setTestBoo(TestBoo.TEST);
        cat.setList(Arrays.asList(6L, 8L));
        cat.setTestList(Arrays.asList("BIG CAT", "small cat"));


        Cat cat1 = new Cat();
        cat1.setId(257L);
        cat1.setDogId(2);
        cat1.setCreateAt(new Date());
        cat1.setTestBoo(TestBoo.TEST);
        cat1.setList(Arrays.asList(1L, 2L));
        cat1.setTestList(Arrays.asList("THR CAT", "moo cat"));

        List<Cat> catList = new ArrayList<>();
        catList.add(cat);
        catList.add(cat1);

        this.catRepository.createBatch(catList);


        return ViewEntity.ok();
    }

    @RequestMapping("/in")
//	@Transactional
    public ViewEntity in() {

        List<Cat> catList = this.catRepository.in(
                InCondition.wrap("testBoo", Arrays.asList(TestBoo.BOO, TestBoo.TEST))
        );
        System.out.println(catList);
        catList.forEach(cat -> System.out.println(cat.getTestBoo()));

        return ViewEntity.ok(catList);
    }

    @RequestMapping("/remote/criteria/test")
    public ViewEntity testCriteria(@RequestBody Criteria criteria) {
        Page<CatTest> page = this.repository.find(criteria);
        return ViewEntity.ok(page);
    }

    @RequestMapping("/oneKey")
    public ViewEntity testOneKey(@RequestBody Long keyOne) {

        System.out.println(keyOne);
        return ViewEntity.ok(keyOne);
    }

    @RequestMapping("/rrc")
    public ViewEntity removeRefreshCreate() {

        Date date = new Date();
        Cat cat1 = new Cat();
        cat1.setId(466);
        cat1.setType("XL");
        cat1.setTestBoo(TestBoo.BOO);
        cat1.setCreateAt(date);

        Cat cat2 = new Cat();
        cat2.setId(251);
        cat2.setType("BL");
        cat2.setTestBoo(TestBoo.BOO);

        List<Cat> list = Arrays.asList(cat1, cat2);

        RemoveRefreshCreate<Cat> wrapper = RemoveRefreshCreate.wrap(list, new Object[]{1, 251});

        String str = JsonX.toJson(wrapper);
        wrapper = JsonX.toObject(str, RemoveRefreshCreate.class);
        System.out.println(wrapper);

        this.catRepository.removeRefreshCreate(wrapper);

        Cat cat = this.catRepository.get(466);

        return ViewEntity.ok();
    }

    @RequestMapping("/test/cache/get")
//	@Transactional
    public ViewEntity testCacheGet() {
//		Cat cat = this.catRepository.get(100);
        Cat cat = new Cat();
        cat.setId(10L);
        cat.setTestBoo(TestBoo.BOO);
        cat.setType("MX");
        this.catRepository.getOne(cat);

        this.catRepository.remove(10L);
        this.catRepository.getOne(cat);
        this.catRepository.getOne(cat);
        this.catRepository.get(10L);
        this.catRepository.get(10L);

        this.catRepository.createBatch(Arrays.asList(cat));
        this.catRepository.getOne(cat);
        this.catRepository.getOne(cat);
        this.catRepository.getOne(cat);
        this.catRepository.get(10L);
        this.catRepository.get(10L);
        this.catRepository.get(10L);

        this.catRepository.refresh(
                RefreshCondition.build().refresh("type", "XXXX").eq("id", 10L)
        );

        this.catRepository.getOne(cat);
        this.catRepository.getOne(cat);
        this.catRepository.getOne(cat);
        this.catRepository.get(10L);
        this.catRepository.get(10L);
        this.catRepository.get(10L);

        return ViewEntity.ok(cat);
    }

}
