package io.xream.x7.demo.controller;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.demo.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;


@RestController
@RequestMapping("/boo")
public class BooController {

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
            throw new RuntimeException("");
        }
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(){
        return this.execute(() -> {
             catService.refresh(
                    RefreshCondition.build().refresh("name","xxx").eq("id",1));
             return "OK";
        });
    }
}
