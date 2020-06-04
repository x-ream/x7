# x7-repo 数据库及数据相关的框架

    x7-rep/x7-lock
    x7-repo/x7-cache
    x7-repo/x7-jdbc-template-plus
    
## x7-lock  分布式锁框架

    代码块: DistributionLock.by(key).lock( o -> {});
    注解: @EnableDistributionLock     @Lock(condition="spEL")
    
    使用场景: 多个服务可能会同时修改同一个资源, 就需要能锁住那个资源
    非常规用: 分布式锁防客户端重复请求(一般用token机制防客户端重复请求)

## x7-cache 缓存框架
   
### 二级缓存 @EnableX7L2Caching

    二级缓存建议返回记录条数不超过20条，如果超过了
    20条，请不要开启二级缓存。
    如果需要开启二级缓存，所有对数据库的写操作项目
    都需要开启二级缓存。
    
    二级缓存是基于multiGet的高速缓存。
    
    包含二级缓存的BaseRepository的API：
        1. in(InCondition)
        2. list(Object)
        3. find(Criteria)
        4. list(Criteria)
        5. get(Id)
        6. getOne(Object)
        
    不含二级缓存的BaseRepository的API:
        1. list()
        2. find(ResultMappedCriteria)
        3. list(ResultMappedCriteria)
        4. listPlainValue(ResultMappedCriteria)
        
    以上设计意味着，如果in和list查询返回记录条数超过20条, 二级缓存
    会失去效果. 
    既然不用二级缓存，如果要返回很多条记录，自定义返回列, 请使用:
        find(ResultMappedCriteria)
        list(ResultMappedCriteria)
        
###  三级缓存 + 一级缓存  @EnableX7L3Caching(waitTimeMills = 1000)  @CacheableL3

     可在项目里单独使用，使用场景:
         1. 报表
         2. 耗性能的远程请求, 且远程数据很少更新，更新时间间隔可预计
         
##  x7-jdbc-template-plus 数据库ORM框架

####    使用方法: 
    @EnableX7Repostory  
    代码片段:
    @Repository
    public interface FooRepository extends BaseRepository<Foo> {}
    
####    实体类注解: 
    @X.Key(定义主键，必选)   @X.Mapping(定义对象和关系映射, 可选)
    
####    BaseRepository API
    
            1. in(InCondition) //in查询, 例如: 页面上需要的主表ID或记录已经查出后，补充查询其他表的文本说明数据时使用
            2. list(Object) //对象查列表
            3. find(Criteria) //标准拼接查询，返回对象形式记录，返回分页对象
            4. list(Criteria) //标准拼接查询，返回对象形式记录，不返回分页对象
            5. get(id) //根据主键查询记录
            6. getOne(Object) //数据库只有一条记录时，就返回那条记录
            7. list() //无条件查全表, 几乎没使用场景
            8. find(ResultMappedCriteria) //标准拼接查询，返回Map形式记录，返回分页对象
            9. list(ResultMappedCriteria) //标准拼接查询，返回Map形式记录，不返回分页对象
            10. listPlainValue(Class<K>, ResultMappedCriteria)//返回没有key的单列数据列表 (结果优化1)
            11. fidnToHandle(ResultMappedCriteria,RowHandler<Map<String,Object>> ) //流处理API
            12. creaet(Object) //插入一条
            13. createBatch(List<Object>) //批量插入
            14. refresh(RefreshCondition) //根据主键更新
            15. refreshUnSafe(RefreshCondition)//不根据主键更新
            16. remove(id)//根据主键删除
            17. removeOrRefreshOrCreate(RemoveOrRrefreshOrCreate<T>) //编辑页面列表时写数据库
            
            
####    标准拼接接口
        CriteriaBuilder // 返回Criteria, 查出对象形式记录
        CriteriaBuilder.ResultMappedBuilder //返回ResultMappedCriteria, 查出Map形式记录，支持连表查询
        RefreshCondition //构建要更新的字段和条件
        
        条件构建的API:
            1. and // AND 默认, 下一个版本可省略，也可不省略
            2. or // OR
            3. eq // =
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
            16. x // 简单的手写sql片段， 例如 foo.amount = bar.price * bar.qty
            17. beginSub // 左括号
            18. endSub // 右括号

        MAP查询结果构建API
            19. distinct //去重
            20. reduce //汇总, 含having入口
            21. groupBy //分组
            22. resultKey //指定返回列
            23. resultKeyFunction //返回列带函数计算
            24. resultWithDottedKey //连表查询返回非JSON格式数据,map的key包含"."  (结果优化2)
           
        连表构建API
            25. sourceScript(joinSql) //简单的连表SQL
            26. sourceScript("order").alia("o") //连表里的主表
            27. sourceScript().source("orderItem").alia("i").joinType(JoinType.INNER_JOIN)
                                              .on("orderId", JoinFrom.wrap("o","id")) //fluent构建连表sql
            28.               .more().[1~18] // LEFT JOIN等, 更多条件
                                           
            
        更新内容构建API
            29. refresh
            
        
        
            
           
            
            
        
        
        
            
        