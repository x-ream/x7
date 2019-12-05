package io.xream.x7.demo.controller;

import io.xream.x7.demo.*;
import io.xream.x7.demo.bean.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import x7.core.bean.*;
import x7.core.bean.condition.RefreshCondition;
import x7.core.repository.CacheableL3;
import x7.core.util.JsonX;
import x7.core.web.Direction;
import x7.core.web.Page;
import x7.core.web.ViewEntity;

import java.util.*;
import java.util.concurrent.TimeUnit;

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


	@RequestMapping("/create")
//	@Transactional
	public ViewEntity create(){

		Cat cat = new Cat();
		cat.setId(245);
		cat.setDogId(2);
		cat.setTestBoo(TestBoo.TEST);

		this.catRepository.create(cat);



//		throw new RuntimeException("-----------------------------> test wawawawa");

		return ViewEntity.ok();
	}

	@RequestMapping("/refresh")
	public ViewEntity refreshByCondition(@RequestBody Cat cat){


//		System.out.println("______: "  + test);
//		CriteriaBuilder builder = CriteriaBuilder.buildCondition();
//		builder.and().eq("type","NL");
//		builder.and().eq("id",2);
//
//		CriteriaCondition criteriaCondition = builder.get();

//		this.catRepository.create(cat);

		List<String> testList = new ArrayList<>();
		testList.add("1111");
		testList.add("2222");

		Dark dark = new Dark();
		dark.setId("33");
		dark.setTest("testKKKKK");

		RefreshCondition<Cat> refreshCondition = new RefreshCondition();
		refreshCondition.and().eq("id",4);
		refreshCondition.refresh("testBoo",TestBoo.TEST);
		refreshCondition.refresh("testList",testList);
		refreshCondition.refresh("testObj",dark);
//		refreshCondition.refresh("createAt",new Date());
		//refreshCondition.refresh("test=test+1");//表达式更新
//		refreshCondition.refresh("test",3333).refresh("type","XL");//赋值更新

//		String str = JsonX.toJson(refreshCondition);
//		refreshCondition = JsonX.toObject(str,RefreshCondition.class);

		boolean flag = this.catRepository.refresh(refreshCondition);//必须带ID更新，没ID报错
//		this.catRepository.refreshUnSafe(refreshCondition);//可以多条更新

//		if (true){
//			throw new RuntimeException("xxxxxxxxxxxxxxxxxxxx");
//		}

		return ViewEntity.ok();
	}


	@RequestMapping("/distinct")
	public ViewEntity distinct(@RequestBody CatRO ro) {

		CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped(CatTest.class,ro);
		builder.distinct("catTest.dogId").distinct("catTest.catFriendName")
				.reduce(Reduce.ReduceType.COUNT,"catTest.id")
				.reduce(Reduce.ReduceType.SUM, "catTest.id")
				.groupBy("catTest.dogId")
				.groupBy("catTest.catFriendName")
		.paged().page(1).rows(2).sort("catTest.dogId",Direction.DESC);
		String sourceScript = "catTest ";
		Criteria.ResultMappedCriteria resultMapped = builder.get();
		resultMapped.setSourceScript(sourceScript);
		Page<Map<String,Object>> page = repository.find(resultMapped);

		return ViewEntity.ok(page);
	}


	@CacheableL3(expireTime = 3, timeUnit = TimeUnit.MINUTES)
	@RequestMapping("/test")
	public ViewEntity test(@RequestBody CatRO ro) {

		{// sample, send the json by ajax from web page
			Map<String, Object> catMap = new HashMap<>();
			catMap.put("id", "");
//			catMap.put("catFriendName", "");
//			catMap.put("time", "");

			Map<String, Object> dogMap = new HashMap<>();
			dogMap.put("number", "");
			dogMap.put("userName", "");

			ro.getResultKeyMap().put("catTest", catMap);
			ro.getResultKeyMap().put("dogTest", dogMap);
		}


		String[] resultKeys = {
				"catTest.id",
				"catTest.catFriendName",
//				"dogTest.number",
				"dogTest.userName"
		};

		ro.setResultKeys(resultKeys);
//		ro.setScroll(true);

//		ro.setResultKeyMap();

		List<Object> inList = new ArrayList<>();
		inList.add("gggg");
		inList.add("xxxxx");
		ro.setOrderBy("catTest.catFriendName,catTest.id");

		Sort sort1 = new Sort();
		sort1.setOrderBy("catTest.catFriendName");
		sort1.setDirection(Direction.ASC);
		Sort sort2 = new Sort();
		sort2.setOrderBy("catTest.id");
		sort2.setDirection(Direction.DESC);
		List<Sort> sortList = new ArrayList<Sort>();
		sortList.add(sort1);
		sortList.add(sort2);

		ro.setOrderBy("catTest.catFriendName,catTest.id");
		ro.setDirection(Direction.DESC);
//		ro.setSortList(sortList);

		CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped(CatTest.class,ro);
		//builder.distinct("catTest.id").reduce(Reduce.ReduceType.COUNT,"catTest.id").groupBy("catTest.id");
		builder.and().in("catTest.catFriendName", inList);
//		builder.paged().orderIn("catTest.catFriendName",inList);//按IN查询条件排序，有值，就过滤掉orderBy
		String sourceScript = "    catTest     LEFT JOIN        dogTest  on catTest.dogId =         dogTest.id";
		Criteria.ResultMappedCriteria resultMapped = builder.get();
		resultMapped.setSourceScript(sourceScript);
		Page<Map<String,Object>> page = repository.find(resultMapped);

//		Cat cat = this.catRepository.get(110);
//
//		System.out.println("____cat: " + cat);
//
//		List<Long> idList = new ArrayList<>();
//		idList.add(109L);
//		idList.add(110L);
//		InCondition inCondition = new InCondition("id",idList);
//		List<Cat> catList = this.catRepository.in(inCondition);

//		System.out.println("____catList: " + catList);

		return ViewEntity.ok(page);

	}


	@RequestMapping("/testAlia")
	public ViewEntity testAlia(@RequestBody CatRO ro) {

		{// sample, send the json by ajax from web page
			Map<String, Object> catMap = new HashMap<>();
			catMap.put("id", "");
//			catMap.put("catFriendName", "");
//			catMap.put("time", "");

			Map<String, Object> dogMap = new HashMap<>();
			dogMap.put("number", "");
			dogMap.put("userName", "");

			ro.getResultKeyMap().put("c", catMap);
			ro.getResultKeyMap().put("d", dogMap);
		}


		String[] resultKeys = {
				"c.id",
				"c.catFriendName",
//				"d.number",
				"d.userName"
		};

		ro.setResultKeys(resultKeys);
//		ro.setScroll(true);

//		ro.setResultKeyMap();

		List<Object> inList = new ArrayList<>();
		inList.add("gggg");
		inList.add("xxxxx");
		ro.setOrderBy("c.catFriendName,c.id");

		Sort sort1 = new Sort();
		sort1.setOrderBy("c.catFriendName");
		sort1.setDirection(Direction.ASC);
		Sort sort2 = new Sort();
		sort2.setOrderBy("c.id");
		sort2.setDirection(Direction.DESC);
		List<Sort> sortList = new ArrayList<Sort>();
		sortList.add(sort1);
		sortList.add(sort2);

		ro.setOrderBy("c.catFriendName,c.id");
		ro.setDirection(Direction.DESC);
//		ro.setSortList(sortList);

		CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped(CatTest.class,ro);
		//builder.distinct("catTest.id").reduce(Reduce.ReduceType.COUNT,"catTest.id").groupBy("catTest.id");
		builder.and().in("c.catFriendName", inList);
//		builder.paged().orderIn("catTest.catFriendName",inList);//按IN查询条件排序，有值，就过滤掉orderBy
		String sourceScript = "catTest c LEFT JOIN dogTest d on c.dogId = d.id";
		Criteria.ResultMappedCriteria resultMapped = builder.get();
		resultMapped.setSourceScript(sourceScript);
		Page<Map<String,Object>> page = repository.find(resultMapped);

//		Cat cat = this.catRepository.get(110);
//
//		System.out.println("____cat: " + cat);
//
//		List<Long> idList = new ArrayList<>();
//		idList.add(109L);
//		idList.add(110L);
//		InCondition inCondition = new InCondition("id",idList);
//		List<Cat> catList = this.catRepository.in(inCondition);

//		System.out.println("____catList: " + catList);

		return ViewEntity.ok(page);

	}



	@RequestMapping("/testOne")
	public ViewEntity testOne(@RequestBody CatRO ro) {


		String[] resultKeys = {
				"id",
				"type"
		};
//		ro.setOrderBy("cat.dogId");

		ro.setResultKeys(resultKeys);

		List<Object> inList = new ArrayList<>();
		inList.add("NL");
		inList.add("BL");

		CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped(Cat.class,ro);
		builder.distinct("id").reduce(Reduce.ReduceType.COUNT,"dogId").groupBy("id");
		builder.and().in("type", inList);
		builder.paged().orderIn("type",inList);


//		builder.or().beginSub().eq("dogTest.userName","yyy")
//				.or().like("dogTest.userName",null)
//				.or().likeRight("dogTest.userName", "xxx")
//				.endSub();
//		builder.or().beginSub().eq("dogTest.userName", "uuu").endSub();


//		String sourceScript = "cat";

		Criteria.ResultMappedCriteria resultMapped = builder.get();

		Page<Map<String,Object>> pagination = repository.find(resultMapped);


		return ViewEntity.ok(pagination);

	}


	public ViewEntity nonPaged(@RequestBody CatRO ro) {

//		CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped(Cat.class);
		CriteriaBuilder builder = CriteriaBuilder.build(Cat.class);

//		builder.resultKey("id").resultKey("type");
		List<Object> inList = new ArrayList<>();
		inList.add("BL");
		inList.add("NL");
		builder.and().in("type",inList);
		builder.paged().orderIn("type",inList);

//		Criteria.ResultMappedCriteria criteria = builder.get();
		Criteria criteria = builder.get();
		Page p = catRepository.find(criteria);

		return ViewEntity.ok(p);
	}


	@RequestMapping("/domain")
    public ViewEntity domain() {

		List<Long> catIdList = new ArrayList<>();
		catIdList.add(2L);
		catIdList.add(3L);

	    CriteriaBuilder.DomainObjectBuilder builder = CriteriaBuilder.buildDomainObject(Cat.class, Mouse.class);
		//根据各种条件，例如ID，查出主对象
	    builder.and().in("id",catIdList);
	    //查出多对多关系的对象
	    builder.domain().relative(CatMouse.class).on("catId").with("mouseId");
	    //已知主对象，根据主对象查出多对多关系的对象
//		builder.domain().known(catIdList).relative(CatMouse.class).on("catId").with("mouseId");
	    Criteria.DomainObjectCriteria criteria = builder.get();

	    //多对多关系查询，仅限于同一域下的对象, 必须遵守面向领域的设计
	    List<DomainObject<Cat,Mouse>> list = this.catRepository.listDomainObject(criteria);

	    String str = JsonX.toJson(list);//测试序列化
		List<DomainObject> test = JsonX.toList(str,DomainObject.class);//测试反序列化
	    System.out.println(test);

	    return ViewEntity.ok(list);
    }



	@RequestMapping(value = "/reyc/test")
	public List<Cat> testRecClient() {

		try {
			TimeUnit.HOURS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<Cat> list = new ArrayList<>();
		return list;
	}

	@RequestMapping(value = "/get")
	public ViewEntity get() {

//		try {
//			TimeUnit.HOURS.sleep(1);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		return ViewEntity.ok(true);
	}

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
	public int getBase(){
//		return "x7-demo/xxx/reyc/str";
		return 10;
	}

	@RequestMapping("/criteria/test")
	public ViewEntity testCtriteria(@RequestBody Criteria criteria){
		return ViewEntity.ok(criteria);
	}

	@RequestMapping("/resultmap/test")
	public ViewEntity testResultMap(@RequestBody Criteria.ResultMappedCriteria criteria){
		return ViewEntity.ok(criteria);
	}

	@RequestMapping("/domain/test")
	public ViewEntity testDomain(@RequestBody Criteria.DomainObjectCriteria criteria){
		return ViewEntity.ok(criteria);
	}

	@RequestMapping("/refreshCondition/test")
	public ViewEntity testRefreshConditionn(@RequestBody RefreshCondition<CatTest> refreshCondition){
		this.repository.refresh(refreshCondition);
		return ViewEntity.ok(refreshCondition);
	}

	@RequestMapping("/listCriteria")
	public ViewEntity listCriteria() {
		//		CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped(Cat.class);
		CriteriaBuilder builder = CriteriaBuilder.build(Cat.class);

//		builder.resultKey("id").resultKey("type");
		List<Object> inList = new ArrayList<>();
		inList.add("BL");
		inList.add("NL");
		builder.and().in("type",inList);
		builder.paged().orderIn("type",inList);

//		Criteria.ResultMappedCriteria criteria = builder.get();
		Criteria criteria = builder.get();
		List<Cat> list = catRepository.list(criteria);

		return ViewEntity.ok(list);
	}
}
