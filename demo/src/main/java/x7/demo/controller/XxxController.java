package x7.demo.controller;


import io.xream.sqli.builder.*;
import io.xream.sqli.page.Page;
import io.xream.x7.base.util.JsonX;
import io.xream.x7.base.web.ViewEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import x7.demo.entity.Cat;
import x7.demo.entity.CatTest;
import x7.demo.entity.TestBoo;
import x7.demo.repository.CatRepository;
import x7.demo.repository.CatTestRepository;
import x7.demo.repository.PetRepository;
import x7.demo.ro.CatRO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static io.xream.sqli.builder.Direction.DESC;
import static io.xream.sqli.builder.JoinType.INNER_JOIN;
import static io.xream.sqli.builder.Op.GT;
import static io.xream.sqli.builder.ReduceType.COUNT_DISTINCT;


@RestController
@RequestMapping("/xxx")
//@Transactional
public class XxxController {

    @Autowired
    private CatTestRepository repository;// sample

    @Autowired
    private CatRepository catRepository;// sample

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CatTestRepository catTestRepository;

    @RequestMapping("/simple/test")
    public void testSimple(){
        this.catRepository.list();
        this.catRepository.get(2);
        this.catRepository.list(new Cat());
    }

    @RequestMapping("/test/rest")
    public ViewEntity testRest(@RequestBody CatTest catTest, HttpServletRequest request) {

        String xid = request.getHeader("TX_XID");

        return ViewEntity.ok("test_rest_ok_" + xid);
    }
    @RequestMapping(value = "/header",method = RequestMethod.GET)
    public ViewEntity testHeader(HttpServletResponse response) {

        response.setHeader("xxxxx","test");

        return ViewEntity.ok(".............");
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
        cat.setId(366L);
        cat.setDogId(3);
        cat.setCreateAt(new Date());
        cat.setTestBoo(TestBoo.BOO);
        cat.setList(Arrays.asList(9L, 11L));
        cat.setTestList(Arrays.asList("BIG xxX CAT", "small cat"));

        this.catRepository.create(cat);

        cat = new Cat();
        cat.setId(367L);
        cat.setDogId(2);
        cat.setCreateAt(new Date());
        cat.setTestBoo(TestBoo.TEST);
        cat.setList(Arrays.asList(1L, 2L));
        cat.setTestList(Arrays.asList("THR CAT", "moo cat"));

        this.catRepository.create(cat);

		return ViewEntity.ok();
    }

    @RequestMapping("/refreshByCondition")
    public ViewEntity refreshByCondition() {

        boolean flag = this.catRepository.refresh(
                RefreshCondition.build()
                        .refresh("testBoo", TestBoo.BOO)
                        .refresh("testList", Arrays.asList("238989","112222"))
                        .refresh("test = test - 3")
                        .refresh("createAt", System.currentTimeMillis())
                        .lt("createAt", 0)
                        .in("id", Arrays.asList(247, 248,512))
//                        .sourceScript("cat LEFT JOIN dogTest on dogTest.id = cat.dogId")
        );//必须带ID更新，没ID报错
//		this.catRepository.refreshUnSafe(refreshCondition);//可以多条更新


        return ViewEntity.ok(flag);
    }


    @RequestMapping("/resultKeyFuntion")
    public ViewEntity resultKeyFuntion(@RequestBody CatRO ro) {

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder.resultWithDottedKey().distinct("catTest.dogId")
                .distinct("catTest.catFriendName")
                .reduce(COUNT_DISTINCT, "catTest.id")
//                .reduce(SUM, "dogTest.petId", Having.of(GT, 1)).groupBy("catTest.xxx")
                .resultKeyFunction(ResultKeyAlia.of("dogTest","petIdSum"),"SUM(dogTest.petId)")
                .groupBy("catTest.xxx")
                .having(ResultKeyAlia.of("dogTest","petIdSum"), GT,1)
                .resultKeyFunction(ResultKeyAlia.of("catTest","xxx"),"YEAR(catTest.time)")
                .sourceScript("FROM catTest INNER JOIN dogTest ON catTest.dogId = dogTest.id")
                .sort(ro.getOrderBy(),ro.getDirection())
                .paged().ignoreTotalRows().page(ro.getPage()).rows(ro.getRows());
        Criteria.ResultMapCriteria resultMapped = builder.build();

        Page<Map<String, Object>> page = repository.find(resultMapped);

        return ViewEntity.ok(page);
    }


    //	@CacheableL3(expireTime = 3, timeUnit = TimeUnit.MINUTES)
    @RequestMapping("/listPlainValue")
    public ViewEntity testListPlainValue(@RequestBody CatRO ro) {

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder.distinct("catTest.id");
        builder.beginSub().gte("dogTest.id", 1).endSub();

        builder.sourceScript("FROM catTest INNER JOIN dogTest ON dogTest.id = catTest.dogId");
        //或者如下
        builder.sourceBuilder().source("catTest");
        builder.sourceBuilder().source("dogTest").join(INNER_JOIN).on("id", JoinFrom.of("catTest", "dogId"));

        Criteria.ResultMapCriteria resultMapped = builder.build();

        List<Long> idList = repository.listPlainValue(Long.class, resultMapped);


        return ViewEntity.ok(idList);

    }


    @RequestMapping("/testAlia")
//    @CacheableL3(expireTime = 2, timeUnit = TimeUnit.MINUTES)
    public ViewEntity testAlia(@RequestBody CatRO ro) {

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
//        builder.distinct("c.dogId").reduce(ReduceType.GROUP_CONCAT_DISTINCT, "c.type").groupBy("c.dogId");
        builder.resultKey("c.id").resultKey("c.dogId","c_id");
        builder.or().eq("d.petId", 0);
        builder.or().lt("c.time",System.currentTimeMillis());
        builder.and().in("c.dogId", Arrays.asList(0));
        builder.sourceScript("catTest c LEFT JOIN dogTest d on c.dogId = d.id");
        builder.sortIn("c.id",Arrays.asList(2,4,3,1,6,5));
        builder.resultWithDottedKey();
        Criteria.ResultMapCriteria resultMapped = builder.build();
        Page<Map<String, Object>> page = repository.find(resultMapped);

        return ViewEntity.ok(page);

    }


    @RequestMapping("/testOne")
    public ViewEntity testOne(@RequestBody CatRO ro) {

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder.distinct("c.id").reduce(COUNT_DISTINCT, "c.dogId").groupBy("c.id");
        builder.and().nin("c.type", Arrays.asList("WHITE", "BLACK"));
        builder.sourceBuilder().source("catTest").alia("c");

        Criteria.ResultMapCriteria resultMapped = builder.build();

        Page<Map<String, Object>> pagination = repository.find(resultMapped);

        return ViewEntity.ok(pagination);

    }


    public ViewEntity nonPaged(@RequestBody CatRO ro) {

        CriteriaBuilder builder = CriteriaBuilder.builder(Cat.class);


        builder.eq("testBoo",TestBoo.TEST).eq("taxType", null)
                .in("type", Arrays.asList("BL","NL"))
                .or().x("(dogId > ? OR test > ?)", 1,1);
        builder.sort("id", DESC);
        builder.sortIn("testBoo", Arrays.asList("TEST","BOO"));//当有sortIn时，sort会被过滤掉
        builder.paged().ignoreTotalRows().page(1).rows(10);

        Criteria criteria = builder.build();

        Page p = catRepository.find(criteria);

        return ViewEntity.ok(p);
    }

    @RequestMapping(value = "/reyc/test")
    public List<Cat> testRecClient() {

        List<Cat> list = new ArrayList<>();
        Cat cat = new Cat();
        cat.setTest(100);
        list.add(cat);
        return list;
    }


    @RequestMapping("/remote/resultmapped/test")
    ViewEntity testResultMappedRemote(@RequestBody Criteria.ResultMapCriteria criteria) {

        System.out.println(criteria);
        Page<Map<String, Object>> page = this.catRepository.find(criteria);

        return ViewEntity.ok(page);
    }

    @RequestMapping("/resultmap/test")
    public ViewEntity testResultMapSimpleSource() {

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder
                .distinct("id")
                .reduce(COUNT_DISTINCT, "dogId")
                .groupBy("id")
        ;

        builder.sort("id", DESC).paged().page(2).rows(2);
        builder.sourceBuilder().source("catTest");
//        builder.sourceScript("catTest");

        Criteria.ResultMapCriteria ResultMapCriteria = builder.build();
        Page<Map<String, Object>> page = this.petRepository.find(ResultMapCriteria); //un workable

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
     
        CriteriaBuilder builder = CriteriaBuilder.builder(Cat.class);

        builder
                .in("testBoo", Arrays.asList("BOO"))
                .eq("type","XXXX")
                .eq("dogId",1)
                .or().in("id", Arrays.asList(247, 248));

        builder.forceIndex("IDX_CAT_DOG_ID");
        builder.sortIn("testBoo",Arrays.asList("BOO"));
        builder.paged().ignoreTotalRows();

        Criteria criteria = builder.build();

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

        boolean flag = this.catRepository.remove(367);
        return ViewEntity.ok(flag);
    }

    @RequestMapping("/createBatch")
//	@Transactional
    public ViewEntity createBatch() {

        Cat cat = new Cat();
        cat.setId(518);
        cat.setDogId(2);
        cat.setCreateAt(new Date());
        cat.setTestBoo(TestBoo.TEST);
        cat.setList(Arrays.asList(6L, 8L));
        cat.setTestList(Arrays.asList("BIG CATX", "small catX"));


        Cat cat1 = new Cat();
        cat1.setId(519);
        cat1.setDogId(2);
        cat1.setCreateAt(new Date());
        cat1.setTestBoo(TestBoo.BOO);
        cat1.setList(Arrays.asList(15L, 2L));
        cat1.setTestList(Arrays.asList("THRa CAT", "moo cat510"));

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
                "testBoo", Arrays.asList(TestBoo.BOO, TestBoo.TEST)
        );

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

        RemoveRefreshCreate<Cat> wrapper = RemoveRefreshCreate.of(list, new Object[]{1, 251});

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


    @RequestMapping(value = "/test/get/{dog}" ,method = RequestMethod.GET)
    public String testGet(@PathVariable String dog){
        return dog;
    }
}
