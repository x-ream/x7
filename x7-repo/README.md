# x7-repo 数据库及数据相关的框架

    x7-repo/x7-lock
    x7-repo/x7-id-generator
    x7-repo/x7-sqli
    x7-repo/x7-jdbc-template-plus
    
## x7-lock  分布式锁框架

    代码块使用
        DistributionLock.by(key).lock(o -> {});
        
    注解使用
        @EnableX7Lock
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
        2. find(q)
        3. list(q)
        4. get(id)
        5. getOne(q)
        
    不含二级缓存的BaseRepository, RepositoryX的API:
        1. list()
        2. find(qx)
        3. list(qx)
        4. listPlainValue(qx)
        
    以上设计意味着，如果in和list查询返回记录条数超过20条, 二级缓存
    会失去高速响应的效果，请务必关闭二级缓存. 
    如果需要返回很多条记录，需要自定义返回列, 请使用:
        find(qx)
        list(qx)
        listPlainValue(qx)
        

         
##  x7-sqli SQL的简单编程接口(SQL INTERFACE)

###    使用方法
    @EnableX7Repostory  
    public class App{
        main()
    
    代码片段:
    @Repository
    public interface FooRepository extends BaseRepository<Foo> {}
    @Repository
    public interface BarRepository extends RepositoryX {}
    
###    实体类注解
    @X.Key //主键, or: @Id, @ID
    private Long id;
    
    @X.Mapping("t_dog_demo") // 可选, 默认表名是 dog; @Column unavailable
    public class Dog {
    
    @X.Mapping("dog_name") // 可选, 默认列名是 name
    private String name;
    
    
###    BaseRepository API
    
            1. in(property, inList) //in查询, 例如: 页面上需要的主表ID或记录已经查出后，补充查询其他表的文本说明数据时使用
            2. list(q) //对象查列表
            3. find(q) //标准拼接查询，返回对象形式记录，返回分页对象
            4. list(q) //标准拼接查询，返回对象形式记录，不返回分页对象
            5. get(Id) //根据主键查询记录
            6. getOne(q) //数据库只有一条记录时，就返回那条记录
            7. list() //无条件查全表, 几乎没使用场景
            8. creaet(Object) //插入一条, 不支持返回自增键, 框架自带ID生成器
            9. createBatch(List<Object>) //批量插入
            10. refresh( qr) //根据主键更新
            11. refreshUnSafe( qr)//不根据主键更新
            12. remove(Id)//根据主键删除
            13. removeRefreshCreate(RemoveRefreshCreate<T>) //编辑页面列表时写数据库
            
###     RepositoryX API
            14. find(xq) //标准拼接查询，返回Map形式记录，返回分页对象
            15. list(xq) //标准拼接查询，返回Map形式记录，不返回分页对象
            16. listPlainValue(Class<K>, qx)//返回没有key的单列数据列表 (结果优化1)
            17. findToHandle(xq, RowHandler<Map<String,Object>>) //流处理API
            
###     QueryBuilder拼接API
        QB // 返回q, 查出对象形式记录
        QB.X //xq, 查出Map形式记录，支持连表查询
        QrB //构建要更新的字段和条件
        
        代码片段:
            {
                QB qb = QB.of(Order.class); 
                qb.eq("userId",obj.getUserId()).eq("status","PAID");
                Q q = qb.build();
                orderRepository.find(q);
            }
        
            {
                QB.X qbx =  QB.x();
                qbx.resultKey("o.id");
                qbx.eq("o.status","PAID");
                qbx.and(sub -> sub.gt("o.createAt",obj.getStartTime()).lt("o.createAt",obj.getEndTime()));
                qbx.or(sub -> sub.eq("o.test",obj.getTest()).or().eq("i.test",obj.getTest()));
                qbx.froms("FROM order o INNER JOIN orderItem i ON i.orderId = o.id");
                qbx.paged(pageBuilder -> pageBuilder.page(obj.getPage()).rows(obj.getSize()));
                Q.X xq = qbx.build();
                orderRepository.find(xq);
            }
            
            {
                orderRepository.refresh(
                    QrB.of(Order.class).refresh("status","PAYING").eq("id",1).eq("status","UN_PAID").build()
                );
            }
        
        条件构建API  (QB | QB.X)
            1. or(sub) // or(sql)
            2. or() // OR
            3. eq // = (eq, 以及其它的API, 值为null，不会被拼接到SQL)
            4. ne // !=
            5. gt // >
            6. gte // >=
            7. lt // <
            8. lte // <=
            9. like //like %xxx%
            10. likeLeft // like xxx%
            11. notLike // not like %xxx%
            12. in // in
            13. nin // not in
            14. isNull // is null
            15. nonNull // is not null
            16. x // 简单的手写sql片段， 例如 x("foo.amount = bar.price * bar.qty") , x("item.quantity = 0")
            17. sub(sql, sub) //
            18. and(sub)

        MAP查询结果构建API  (QB.X)
            19. distinct //去重
            20. reduce //归并计算
                    // .reduce(ReduceType.SUM, "dogTest.petId")
                    //含Having接口 (仅仅在reduc查询后,有限支持Having)
            21. groupBy //分组
            22. select //指定返回列
            23. selectWithFunc //返回列函数支持
                    // .selectWithFunc(ResultKeyAlia.of("o","at"),"FFF(o.createAt, ?)", 100000) 
            24. resultWithDottedKey //连表查询返回非JSON格式数据,map的key包含"."  (结果优化2)
           
        连表构建API  (QB.X)
            25. from(joinSql) //简单的连表SQL，不支持LEFT JOIN  ON 多条件; 多条件，请用API[28]
            26. fromBuilder.of(Order.class,"o") //连表里的主表, API: .fromX(FromX fromX)
            27. fromBuilder.JOIN(LEFT).of(OrderItem.class,"i")
                                              .on("i.orderId = o.id", 
            28                  on -> on.gt(...)) //LEFT JOIN等, 更多条件
            29. fromBuilder.sub(....,"i").JOIN("ANY INNER JOIN").on(....) //fluent构建连表sql
        
        分页及排序API  (QB | QB.X)
            30. sort("o.id", Direction.DESC)
            31. paged(pb -> pb.ignoreTotalRows().page(1).rows(10).last(10000)) //设置last(long),会忽略page(int); 
                                           
        更新构建API  ( qr)
            32. refresh
            
        框架优化
            froms/fromBuilder
                如果条件和返回都不包括sourceScript里的连表，框架会优化移除连接（但目标连接表有用时，中间表不会
                被移除）。
                关闭优化: qb.withoutOptimization()
            in
                每500个条件会切割出一次in查询
            
        不支持项
            union 
