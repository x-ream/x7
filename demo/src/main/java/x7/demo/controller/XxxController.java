package x7.demo.controller;


import io.xream.internal.util.JsonX;
import io.xream.sqli.builder.*;
import io.xream.sqli.converter.ResultMapToBean;
import io.xream.sqli.page.Page;
import io.xream.sqli.util.SqliJsonUtil;
import io.xream.x7.base.web.ViewEntity;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import x7.demo.entity.Cat;
import x7.demo.entity.CatTest;
import x7.demo.entity.TestBoo;
import x7.demo.repository.CatRepository;
import x7.demo.repository.CatTestRepository;
import x7.demo.repository.PetRepository;
import x7.demo.ro.CatCreateRo;
import x7.demo.ro.CatFindRo;
import x7.vo.CatDogVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static io.xream.sqli.builder.Direction.DESC;
import static io.xream.sqli.builder.JoinType.INNER_JOIN;
import static io.xream.sqli.builder.Op.GT;
import static io.xream.sqli.builder.ReduceType.COUNT_DISTINCT;


@RestController
@RequestMapping("/xxx")
//@Transactional
public class XxxController {

    private static final Logger logger = LoggerFactory.getLogger(XxxController.class);

    @Autowired
    private CatTestRepository repository;// sample

    @Autowired
    private CatRepository catRepository;// sample

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CatTestRepository catTestRepository;

    @RequestMapping("/id/create")
    public long createId(){
        return this.catRepository.createId();
    }

    @RequestMapping("/simple/test")
    public void testSimple(){
        List<Cat> list = this.catRepository.list();
        list.stream().forEach(cat -> {
            System.out.println(cat);
        });
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
    public ViewEntity createCat(@RequestBody CatCreateRo ro) throws Exception{
        Cat cat = new Cat();
        BeanUtils.copyProperties(cat, ro);
        this.catRepository.create(cat);
        return ViewEntity.ok();
    }

    @RequestMapping("/createOrReplace")
//	@Transactional
    public ViewEntity refreshOrCreat(@RequestBody Cat cat) {
        boolean flag = this.catRepository.createOrReplace(cat);
        return ViewEntity.ok(flag);
    }

    public ViewEntity listCat(){
        List<Cat> list = this.catRepository.list();

        return ViewEntity.ok(list);
    }

    @RequestMapping("/create")
    @Transactional
    public ViewEntity create() {

        Cat cat = new Cat();
        cat.setId(540L);
        cat.setDogId(3L);
        cat.setCreateAt(LocalDate.now());
        cat.setTestBoo(TestBoo.BOO);
        cat.setList(Arrays.asList(9L, 11L));
        cat.setTestList(Arrays.asList("BIG xxX CAT", "small cat"));

        this.catRepository.create(cat);

		return ViewEntity.ok();
    }

    @RequestMapping("/refreshByCondition")
    public ViewEntity refreshByCondition() {
        LocalDate localDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
        RefreshCondition<Cat> refreshCondition =
                RefreshBuilder.builder()
                        .refresh("testBoo", "B")
                        .refresh("testList", Arrays.asList("ZZZZZ","xxxx"))
                        .refresh("test = test - 3").refresh("isDone",true)
                        .refresh("createAt", System.currentTimeMillis())
                        .lte("createAt", System.currentTimeMillis())
                        .in("id", Arrays.asList(null,247, 248,513)).eq("testBoo",TestBoo.BOO).build();

        String jackStr = SqliJsonUtil.toJson(refreshCondition);
        refreshCondition = SqliJsonUtil.toObject(jackStr,RefreshCondition.class);
        boolean flag = this.catRepository.refreshUnSafe(
         refreshCondition
//                .lt("id",10)
//                        .sourceScript("cat LEFT JOIN dogTest on dogTest.id = cat.dogId")
        );//必须带ID更新，没ID报错
//		this.catRepository.refreshUnSafe(refreshCondition);//可以多条更新

        return ViewEntity.ok(flag);
    }


    @RequestMapping("/resultKeyFuntion")
    public ViewEntity resultKeyFuntion(@RequestBody CatFindRo ro) {

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
                .paged().ignoreTotalRows().page(ro.getPage()).rows(ro.getRows()).last(ro.getLast());
        Criteria.ResultMapCriteria resultMapped = builder.build();

        List<Map<String, Object>> list = null;
        list = this.petRepository.list(resultMapped);// ONCE BUILD, ONCE USE
        return ViewEntity.ok(list);
    }


    //	@CacheableL3(expireTime = 3, timeUnit = TimeUnit.MINUTES)
    @RequestMapping("/listPlainValue")
    public ViewEntity testListPlainValue(@RequestBody CatFindRo ro) {

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder.distinct("catTest.id");
        builder.beginSub().gte("dogTest.id", 1).endSub();

        builder.sourceScript("FROM catTest INNER JOIN dogTest ON dogTest.id = catTest.dogId");
        //或者如下
        builder.sourceBuilder().source("catTest");
        builder.sourceBuilder().source("dogTest").join(INNER_JOIN).on("id", JoinFrom.of("catTest", "dogId"));

        Criteria.ResultMapCriteria resultMapped = builder.build();

        List<Long> idList = repository.listPlainValue(Long.class, resultMapped);
        this.petRepository.listPlainValue(Long.class, resultMapped);

        return ViewEntity.ok(idList);

    }


    @RequestMapping("/testAlia")
//    @CacheableL3(expireTime = 2, timeUnit = TimeUnit.MINUTES)
    public ViewEntity testAlia(@RequestBody CatFindRo ro) {

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
//        builder.distinct("c.dogId").reduce(ReduceType.GROUP_CONCAT_DISTINCT, "c.type").groupBy("c.dogId");
        builder.resultKey("c.id").resultKey("c.dogId","c_id").resultKey("c.time","tt").resultKey("c.isCat","isCat");
        builder.resultKey("d.id").resultKey("d.number","nA");
        builder.eq("d.petId", 0);
        builder.eq("c.dogId",0);
        builder.lt("c.time",System.currentTimeMillis());
        builder.and().in("c.dogId", Arrays.asList(1));
        builder.sourceScript("catTest c INNER JOIN dogTest d on c.dogId = d.id");
        builder.sortIn("c.id",Arrays.asList(2,4,3,1,6,5));
        Criteria.ResultMapCriteria resultMapped = builder.build();
        Page<Map<String, Object>> page = repository.find(resultMapped);

//        builder.resultWithDottedKey(); //以下测试不支持resultWithDottedKey
        List<Map<String,Object>> list = this.petRepository.list(resultMapped);//增加独立的ResultMapRepository测试

        list.stream().forEach(
                map -> {
                    CatDogVo vo = ResultMapToBean.copy(CatDogVo.class, map);
                    System.out.println(vo);
                }
        );


        return ViewEntity.ok(page);

    }


    @RequestMapping("/testOne")
    public ViewEntity testOne(@RequestBody CatFindRo ro) {

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder.distinct("c.id").reduce(COUNT_DISTINCT, "c.dogId").groupBy("c.id");
        builder.and().nin("c.testBoo", Arrays.asList(TestBoo.BOO, TestBoo.HLL))
        .eq("c.userId",0);
        builder.sourceBuilder().source("cat").alia("c");

        Criteria.ResultMapCriteria resultMapped = builder.build();

        Page<Map<String, Object>> pagination = catRepository.find(resultMapped);

        return ViewEntity.ok(pagination);

    }


    public ViewEntity nonPaged(@RequestBody CatFindRo ro) {

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();

//        builder.in("testBoo",Arrays.asList(
//                "TEST ' ); DELETE FROM t_pig; SELECT * FROM t_cat WHERE 1 = 1 OR test_boo IN ('ddd"
//        ));
//        builder.in("testBoo",Arrays.asList("TEST') UNION SELECT * FROM t_cat WHERE 1 = 1 OR test_boo IN ('ddd"));
        builder.x("userId>=((((((id*10))))+@xxx-1)) AND id=1");
        builder.resultKey("id").resultKey("testBoo");
//        builder.sort("id", DESC);
        builder.xAggr("ORDER BY id DESC");
        builder.eq("dogId",0);
        builder.sourceBuilder().source("cat");//CatRepository.class
        builder.paged().ignoreTotalRows().page(1).rows(10);

        Criteria.ResultMapCriteria resultMapCriteria = builder.build();

        Page p = catRepository.find(resultMapCriteria);

        Cat cat = this.catRepository.get(0);
        System.out.println(cat);

        return ViewEntity.ok(p);
    }

    @RequestMapping(value = "/reyc/test")
    public List<Cat> testRecClient() {

        List<Cat> list = new ArrayList<>();
        Cat cat = new Cat();
        cat.setTest(100L);
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

        builder.sort("id", DESC).paged().page(2).rows(2).last(4);
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

    @RequestMapping("/listWithEnum")
    public ViewEntity listWithEnum() {
     
        CriteriaBuilder builder = CriteriaBuilder.builder(Cat.class);

        builder
                .in("testBoo", Arrays.asList("BOO"))
                .x("testBoo = ?",TestBoo.BOO)
                .eq("dogId",1)
                .or().in("id", Arrays.asList(247, 248));

        builder.sortIn("testBoo",Arrays.asList(TestBoo.BOO));
        builder.paged().ignoreTotalRows();

        Criteria criteria = builder.build();

        Page<Cat> page = catRepository.find(criteria);

        return ViewEntity.ok(page);
    }

    @RequestMapping("/list")
    public ViewEntity list() {
        this.catRepository.list();
        return ViewEntity.ok();
    }

    @RequestMapping("/remove")
    public ViewEntity remove() {

        boolean flag = this.catRepository.remove(545);
        return ViewEntity.ok(flag);
    }

    @RequestMapping("/createBatch")
//	@Transactional
    public ViewEntity createBatch() {

        Cat cat = new Cat();
        cat.setId(546L);
        cat.setDogId(3L);
        cat.setName("44444L");
        cat.setCreateAt(LocalDate.now());
        cat.setTestBoo(TestBoo.HLL);
        cat.setList(Arrays.asList(2L, 11L));
        cat.setTestList(Arrays.asList("d BIG CATX", "small catX"));


        Cat cat1 = new Cat();
        cat1.setId(547L);
        cat1.setDogId(2L);
        cat1.setCreateAt(LocalDate.now());
        cat1.setTestBoo(TestBoo.BOO);
        cat1.setList(Arrays.asList(15L, 2L));
        cat1.setTestList(Arrays.asList("r THRa CAT", "moo cat510"));

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

        Cat cat1 = new Cat();
        cat1.setId(619L);
        cat1.setType("XL");
        cat1.setTestBoo(TestBoo.BOO);
        cat1.setCreateAt(LocalDate.now());

        Cat cat2 = new Cat();
        cat2.setId(620L);
        cat2.setType("BL");
        cat2.setTestBoo(TestBoo.BOO);

        List<Cat> list = Arrays.asList(cat1, cat2);

        RemoveRefreshCreate<Cat> wrapper = RemoveRefreshCreate.of(list, new Object[]{471});

        String str = SqliJsonUtil.toJson(wrapper);
        wrapper = SqliJsonUtil.toObject(str, RemoveRefreshCreate.class);
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
                RefreshBuilder.builder().refresh("type", "XXXX").eq("id", 10L).build()
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

    @RequestMapping("/in/test")
    public ViewEntity testInCondition(){
        InCondition inCondition = InCondition.of("testBoo",Arrays.asList(TestBoo.BOO));
        String str = JsonX.toJson(inCondition);
        inCondition = SqliJsonUtil.toObject(str, InCondition.class);

        List<Cat> list = this.catRepository.in("testBoo", inCondition.getInList());

        return ViewEntity.ok(list);
    }

    @RequestMapping(value = "/time/test", method = RequestMethod.GET)
    public Boolean testTime(){
        logger.info(new Date().toString());
        return true;
    }

}
