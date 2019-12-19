# x7
   [http://x7.xream.io](http://x7.xream.io)
   
[![license](https://img.shields.io/github/license/x-ream/x7.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![maven](https://img.shields.io/maven-central/v/io.xream.x7/x7-parent.svg)](https://search.maven.org/search?q=io.xream)

       
       x7-repository
          @EnableX7Repository           @Repository       and { interface FooRepository extends BaseRepository<Foo> }
          @EnableX7L3Caching            @CacheableL3
          @EnableDistributionLock       @Lock             or  
             { DistributionLock.by(key).lock(task) }
          
       x7-reyc
          /reyc  (wrapped Resilience4J)
             @EnableReySupport                            and { private ReyTemplate reyTemplate }
             @EnableReyClient           @ReyClient
           
          /reliable  (mq transaction api)
             @EnableReliabilityManagement     
                 @ReliableProducer
                 @ReliableOnConsumed
        
       x7-spring-boot-starter
       
       x7-seata-spring-boot-starter
       
       
## Notes
       A method, coded with io.xream/reliable or seata, maybe we can not use:
            @Lock  or 
            { DistributionLock.by(key).lock(task) }
            
            