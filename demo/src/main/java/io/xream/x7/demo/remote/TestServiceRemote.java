package io.xream.x7.demo.remote;


import io.xream.x7.demo.CatRO;
import io.xream.x7.demo.bean.Cat;
import io.xream.x7.reyc.ReyClient;
import io.xream.x7.reyc.Url;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import x7.core.bean.Criteria;
import x7.core.bean.condition.RefreshCondition;
import x7.core.web.ViewEntity;

import java.util.List;


@ReyClient(value = "http://${web.demo}/xxx", circuitBreaker = "", retry = true, fallback = TestFallback.class)
public interface TestServiceRemote {


    @RequestMapping(value = "/reyc/test")
    Boolean test(CatRO ro, Url url);

    @RequestMapping(value = "/reyc/test")
    List<Cat> testFallBack(CatRO ro);

    @RequestMapping(value = "/time/test", method = RequestMethod.GET)
    Boolean testTimeJack();

    @RequestMapping(value = "/reyc/base", method = RequestMethod.GET)
    int getBase();

    @RequestMapping("/criteria/test")
    ViewEntity testCriteria(Criteria criteria);

    @RequestMapping("/resultmap/test")
    ViewEntity testResultMap(Criteria.ResultMappedCriteria criteria);

    @RequestMapping("/domain/test")
    ViewEntity testDomain(Criteria.DomainObjectCriteria criteria);


    @RequestMapping("/refreshCondition/test")
    ViewEntity testRefreshConditionn( RefreshCondition refreshCondition);

}
