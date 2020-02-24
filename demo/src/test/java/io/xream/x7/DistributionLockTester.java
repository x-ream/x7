package io.xream.x7;

import io.xream.x7.demo.bean.Cat;
import org.springframework.stereotype.Component;
import io.xream.x7.lock.Lock;

@Component
public class DistributionLockTester {

    @Lock(condition = "#cat.id + '_' + #cat.getListX()",timeout = 1000000)
    public String test(Cat cat) {

        System.out.println(" LOCKED OK");

        return "LOCKED OK";
    }
}
