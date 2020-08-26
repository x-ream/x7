package x7.demo.remote;

import io.xream.x7.reyc.Url;
import x7.demo.ro.CatRO;

public class TestFallback {

    public void testFallBack(CatRO ro){

        /*
         * send to kafka
         */

        System.out.println("fallBack");
    }

    public void test(CatRO ro, Url url){

        /*
         * send to kafka
         */

        System.out.println("fallBack with Url");
    }
}
