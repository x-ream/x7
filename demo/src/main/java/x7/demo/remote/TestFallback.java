package x7.demo.remote;

import io.xream.x7.reyc.Url;
import x7.demo.ro.CatFindRo;

public class TestFallback {

    public void testFallBack(CatFindRo ro){

        /*
         * send to kafka
         */

        System.out.println("fallBack");
    }

    public void test(CatFindRo ro, Url url){

        /*
         * send to kafka
         */

        System.out.println("fallBack with Url");
    }
}
