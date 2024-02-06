# x7
   [http://x7.xream.io](http://x7.xream.io) 
   
[![license](https://img.shields.io/github/license/x-ream/x7.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![maven](https://img.shields.io/maven-central/v/io.xream.x7/x7-parent.svg)](https://search.maven.org/search?q=io.xream)
![springboot](https://img.shields.io/badge/springboot-v3.1.6-green.svg)
![jdk](https://img.shields.io/badge/jdk-v17.0.8-orange.svg)

   x7/x7-repo [DETAILED README](https://github.com/x-ream/x7/blob/master/x7-repo/README.md)


### GLIMPSE
       
       x7-repo     (sqli,  Spring JdbcTemplate, Distribution Lock, Cache and so on....)
          @EnableX7Repository           @Repository       and { interface FooRepository extends BaseRepository<Foo> }
          @EnableX7Lock                 @Lock             or  
             { DistributionLock.by(key).lock(task) }

        
## [x7-spring-boot-starter](https://github.com/x-ream/x7-spring-boot-starter)


###  如何使用第三方id生成器
       1. @SpringBootApplication(exclude = IdGeneratorAutoConfiguration.class)
       2. 参照x7-id-generator工程， 新建工程，实现自定义的IdGeneratorService, 代码如下:
            public interface MyIdGeneratorService extends IdGeneratorProxy       
       
### NOTES
       1. A method, coded with io.xream/acku or seata, maybe we can not use:
            @Lock  or 
            { DistributionLock.by(key).lock(task) }
 
            