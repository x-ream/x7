# x7
   [http://x7.xream.io](http://x7.xream.io)
   
[![license](https://img.shields.io/github/license/x-ream/x7.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![maven](https://img.shields.io/maven-central/v/io.xream.x7/x7-parent.svg)](https://search.maven.org/search?q=io.xream)

       x7-core
  
       x7-repository
          extends BaseRepository<Foo>
          @EnableX7Repository
          @EnableX7L3Caching
          
       x7-reyc
          /reyc
             @EnableReyClient  or  @EnableReySupport
             @ReyClient
          /reliable
             @EnableReliabilityManagement
             @ReliableProducer
             @ReliableOnConsumed
