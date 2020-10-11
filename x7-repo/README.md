# x7-repo 数据库及数据相关的框架

    x7-repo/x7-lock
    x7-repo/x7-cache
    x7-repo/x7-sqli
    x7-repo/x7-jdbc-template-plus
    
## x7-lock  分布式锁框架

    代码块使用
        DistributionLock.by(key).lock(o -> {});
        
    注解使用
        @EnableDistributionLock
        public class App{
            main()
         
        @Lock(condition="#foo.getId()") //需要自定义condition
        public void doSomething(Foo foo)
    
    使用场景: 多个服务可能会同时修改同一个资源, 就需要能锁住那个资源
    非常规用: 分布式锁防客户端重复请求(一般用token机制防客户端重复请求)

## x7-cache 缓存框架
   
### 二级缓存 

    注解使用
        @EnableX7L2Caching
        public class App{
            main()

    二级缓存是基于redis.multiGet的高速缓存实现。

    二级缓存建议返回记录条数不超过20条。调用带二级缓存的API，返回记录条数超过了
    20条，请关闭二级缓存。
    如果需要开启二级缓存，所有对数据库的写操作项目都需要开启二级缓存。
    
    包含二级缓存的BaseRepository的API：
        1. in(property, inList)
        2. list(Object)
        3. find(Criteria)
        4. list(Criteria)
        5. get(Id)
        6. getOne(Object)
        
    不含二级缓存的BaseRepository, ResultMapRepository的API:
        1. list()
        2. find(ResultMapCriteria)
        3. list(ResultMapCriteria)
        4. listPlainValue(ResultMapCriteria)
        
    以上设计意味着，如果in和list查询返回记录条数超过20条, 二级缓存
    会失去高速响应的效果，请务必关闭二级缓存. 
    如果需要返回很多条记录，需要自定义返回列, 请使用:
        find(ResultMapCriteria)
        list(ResultMapCriteria)
        listPlainValue(ResultMapCriteria)
        
        
###  三级缓存 + 一级缓存  

     注解使用
        @EnableX7L3Caching(waitTimeMills = 1000)
        public class App{
            main()
     
        @CacheableL3(expireTime = 1000, condition="#foo.getId()") //默认condition为对象JSON的MD5值
        public void doSomething(Foo foo)

     可在项目里单独使用，使用场景:
         1. 报表
         2. 耗性能的远程请求, 且远程数据很少更新，更新时间间隔可预计
         
##  x7-sqli SQL的简单编程接口(SQL INTERFACE)

###    使用方法
    @EnableX7Repostory  
    public class App{
        main()
    
    代码片段:
    @Repository
    public interface FooRepository extends BaseRepository<Foo> {}
    @Repository
    public interface BarRepository extends ResultMapRepository {}
    
###    实体类注解
    @X.Key //主键, 必须
    private Long id;
    
    @X.Mapping("t_dog_demo") // 可选, 默认表名是 dog
    public class Dog {
    
    @X.Mapping("dog_name") // 可选, 默认列名是 name
    private String name;
    
    
###    BaseRepository API
    
            1. in(property, inList) //in查询, 例如: 页面上需要的主表ID或记录已经查出后，补充查询其他表的文本说明数据时使用
            2. list(Object) //对象查列表
            3. find(Criteria) //标准拼接查询，返回对象形式记录，返回分页对象
            4. list(Criteria) //标准拼接查询，返回对象形式记录，不返回分页对象
            5. get(Id) //根据主键查询记录
            6. getOne(Object) //数据库只有一条记录时，就返回那条记录
            7. list() //无条件查全表, 几乎没使用场景
            8. creaet(Object) //插入一条, 不支持返回自增键, 框架自带ID生成器
            9. createBatch(List<Object>) //批量插入
            10. refresh(RefreshCondition) //根据主键更新
            11. refreshUnSafe(RefreshCondition)//不根据主键更新
            12. remove(Id)//根据主键删除
            13. removeRefreshCreate(RemoveRefreshCreate<T>) //编辑页面列表时写数据库
            
### ResultMapRepository API
            14. find(ResultMapCriteria) //标准拼接查询，返回Map形式记录，返回分页对象
            15. list(ResultMapCriteria) //标准拼接查询，返回Map形式记录，不返回分页对象
            16. listPlainValue(Class<K>, ResultMapCriteria)//返回没有key的单列数据列表 (结果优化1)
            17. findToHandle(ResultMapCriteria, RowHandler<Map<String,Object>>) //流处理API
            
###    标准拼接API
        CriteriaBuilder // 返回Criteria, 查出对象形式记录
        CriteriaBuilder.ResultMapBuilder //返回ResultMapCriteria, 查出Map形式记录，支持连表查询
        RefreshCondition //构建要更新的字段和条件
        
        代码片段:
            {
                CriteriaBuilder builder = CriteriaBuilder.builder(Order.class); 
                builder.eq("userId",obj.getUserId()).eq("status","PAID");
                Criteria criteria = builer.build();
                orderRepository.find(criteria);
            }
        
            {
                CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
                builder.resultKey("o.id");
                builder.eq("o.status","PAID");
                builder.beginSub().gt("o.createAt",obj.getStartTime()).lt("o.createAt",obj.getEndTime()).endSub();
                builder.beginSub().eq("o.test",obj.getTest()).or().eq("i.test",obj.getTest()).endSub();
                builder.sourceScript("FROM order o INNER JOIN orderItem i ON i.orderId = o.id");
                builder.paged(obj);
                Criteria.ResultMapCriteria criteria = builder.build();
                orderRepository.find(criteria);
            }
            
            {
                orderRepository.refresh(
                    RefreshCondition.build().refresh("status","PAYING").eq("id",1).eq("status","UN_PAID")
                );
            }
        
        条件构建API  (CriteriaBuilder | ResultMapBuilder)
            1. and // AND 默认, 可省略，也可不省略
            2. or // OR
            3. eq // = (eq, 以及其它的API, 值为null，不会被拼接到SQL)
            4. ne // !=
            5. gt // >
            6. gte // >=
            7. lt // <
            8. lte // <=
            9. like //like %xxx%
            10. likeRight // like xxx%
            11. notLike // not like %xxx%
            12. in // in
            13. nin // not in
            14. isNull // is null
            15. nonNull // is not null
            16. x // 简单的手写sql片段， 例如 x("foo.amount = bar.price * bar.qty") , x("item.quantity = 0")
            17. beginSub // 左括号
            18. endSub // 右括号

        MAP查询结果构建API  (ResultMapBuilder)
            19. distinct //去重
            20. reduce //归并计算
                    // .reduce(ReduceType.SUM, "dogTest.petId") 
                    // .reduce(ReduceType.SUM, "dogTest.petId", Having.of(Op.GT, 1))
                    //含Having接口 (仅仅在reduc查询后,有限支持Having)
            21. groupBy //分组
            22. resultKey //指定返回列
            23. resultKeyFunction //返回列函数支持
                    // .resultKeyFunction(ResultKeyAlia.of("o","at"),"FFF(o.createAt, ?)", 100000) 
            24. resultWithDottedKey //连表查询返回非JSON格式数据,map的key包含"."  (结果优化2)
           
        连表构建API  (ResultMapBuilder)
            25. sourceScript(joinSql) //简单的连表SQL，不支持LEFT JOIN  ON 多条件; 多条件，请用API[28]
            26. sourceBuilder.source("order").alia("o") //连表里的主表
            27. sourceBuilder().source("orderItem").alia("i").join(JoinType.LEFT_JOIN)
                                              .on("orderId", JoinFrom.of("o","id")) //fluent构建连表sql
            28.               .more().[1~18] // LEFT JOIN等, 更多条件
            29. sourceBuilder().sub(....) // INNER JOIN (....) i  有限支持clickhouse等数据库
                            .alia("i").join("ANY INNER JOIN").on("orderId", JoinFrom.of("o","id")) //fluent构建连表sql
        
        分页及排序API  (CriteriaBuilder | ResultMapBuilder)
            30. sort("o.id", Direction.DESC)
            31. paged().ignoreTotalRows().page(1).rows(10) 
                                           
        更新构建API  (RefreshCondition)
            32. refresh
            
        框架优化
            sourceScript/sourceBuilder
                如果条件和返回都不包括sourceScript里的连表，框架会优化移除连接（但目标连接表有用时，中间表不会
                被移除）。
                关闭优化: builder.withoutOptimization()
            in
                每500个条件会切割出一次in查询
            
        不支持项
            in(sql) // 和连表查询及二级缓存的设计有一定的冲突
            union // 过于复杂
            
                