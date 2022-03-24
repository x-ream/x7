package x7.demo.controller;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.xream.internal.util.ExceptionUtil;
import io.xream.sqli.builder.RefreshBuilder;
import io.xream.x7.base.web.ViewEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import x7.demo.entity.DogTest;
import x7.demo.remote.TestServiceRemote;
import x7.demo.service.CatService;
import x7.demo.service.DogService;

import javax.annotation.Resource;
import java.util.concurrent.Callable;


@RestController
@RequestMapping("/boo")
public class BooController {

    private Logger logger = LoggerFactory.getLogger(BooController.class);

    private final RateLimiter rateLimiter;

    @Autowired
    private CatService catService;
    @Autowired
    private DogService dogService;
    @Resource
    private TestServiceRemote testServiceRemote;


    public BooController(RateLimiterRegistry rateLimiterRegistry){
        rateLimiter = rateLimiterRegistry.rateLimiter("boo");
    }

    private <T> T execute(Callable<T> callable) {
        try{
            return RateLimiter.decorateCallable(rateLimiter, callable).call();
        }catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ViewEntity test(){
        return this.execute(() -> {
             catService.refresh(
                    RefreshBuilder.builder().refresh("name","xxx").eq("id",1).build()
             );
             return ViewEntity.ok("REFRESHED");
        });
    }

//    @io.github.resilience4j.ratelimiter.annotation.RateLimiter(name = "opp",fallbackMethod = "testFallback")
    @RequestMapping(value = "/opp", method = RequestMethod.GET)
    public ViewEntity opp(){
        logger.info("opp");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-TOKEN","szdaasddie2232");
        boolean flag = testServiceRemote.testTimeJack(httpHeaders);
        logger.info("opp: " + flag);
        return ViewEntity.ok("xxxx");
    }

    public ViewEntity testFallback(Throwable throwable){
        logger.info("fallback....." + throwable == null? "null" : throwable.getMessage());
        return ViewEntity.ok("FALLBACK");
    }


    public ViewEntity lock(){
        DogTest dogTest = new DogTest();
        dogTest.setUserName("XXXX");
        this.dogService.lock0(dogTest);
        return ViewEntity.ok();
    }
}
