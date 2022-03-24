package x7.demo.remote;


import io.xream.rey.annotation.ReyClient;
import io.xream.sqli.builder.Criteria;
import io.xream.sqli.builder.RefreshCondition;
import io.xream.x7.base.web.ViewEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import x7.demo.entity.Cat;
import x7.demo.ro.CatFindRo;

import java.util.List;


@ReyClient(value = "http://${web.demo}/xxx",  retry = true, fallback = TestFallback.class, groupRouter = CatServiceGroupRouterForK8S.class)
public interface TestServiceRemote {


    @RequestMapping(value = "/reyc/test")
    List<Cat> testFallBack(CatFindRo ro);

    @RequestMapping(value = "/time/test", method = RequestMethod.GET, headers = {"X-SN:1001"})
    Boolean testTimeJack(HttpHeaders headers);

    @RequestMapping(value = "/reyc/base", method = RequestMethod.GET)
    int getBase();

    @RequestMapping("/remote/criteria/test")
    ViewEntity testCriteriaRemote(Criteria criteria);

    @RequestMapping("/remote/resultmapped/test")
    ViewEntity testResultMappedRemote(Criteria.ResultMapCriteria criteria);

    @RequestMapping("/remote/refreshCondition/test")
    ViewEntity testRefreshConditionnRemote( RefreshCondition refreshCondition);

    @RequestMapping("/refresh")
    ViewEntity refreshByCondition(Cat cat);


    @RequestMapping("/create")
    ViewEntity create();

    @RequestMapping("/get")
    ViewEntity get();

    @RequestMapping("/oneKey")
    ViewEntity testOneKey(Long keyOne);
}
