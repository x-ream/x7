package io.xream.x7;

import io.xream.x7.lock.Lock;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DistributionLockTester {

    @Lock(abortingIfNoLock = true)
    public String test(String cat) {

        System.out.println(" LOCKED OK");

        try {
            TimeUnit.MINUTES.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "LOCKED OK";
    }
}
