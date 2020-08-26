package x7.demo.controller;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.xream.sqli.builder.RefreshCondition;
import io.xream.x7.base.util.ExceptionUtil;
import io.xream.x7.common.web.ViewEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import x7.demo.service.CatService;

import java.util.concurrent.Callable;


@RestController
@RequestMapping("/boo")
public class BooController {

    private Logger logger = LoggerFactory.getLogger(BooController.class);

    private final RateLimiter rateLimiter;

    @Autowired
    private CatService catService;


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
                    RefreshCondition.build().refresh("name","xxx").eq("id",1)
             );
             return ViewEntity.ok("REFRESHED");
        });
    }

    @io.github.resilience4j.ratelimiter.annotation.RateLimiter(name = "opp",fallbackMethod = "testFallback")
    @RequestMapping(value = "/opp", method = RequestMethod.GET)
    public ViewEntity opp(){
        logger.info("opp");
        return ViewEntity.ok("xxxx");
    }

    public ViewEntity testFallback(Throwable throwable){
        logger.info("fallback....." + throwable == null? "null" : throwable.getMessage());
        return ViewEntity.ok("FALLBACK");
    }
}
