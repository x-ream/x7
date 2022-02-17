package x7;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import x7.demo.entity.TestBoo;
import x7.demo.remote.OrderRemote;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AppTest {

    @Autowired
    private XxxTest xxxTest;

    @Resource(name = "x7.demo.remote.OrderRemote")
    private OrderRemote orderRemote;

    @Test
    public void testMoreInstance(){
        System.out.println(this.orderRemote.verify());
    }


    @Test
    public void testAll() throws Exception {

        Assert.isTrue(TestBoo.BOO != TestBoo.HLL, "XXX");

//        xxxTest.createId();
//        xxxTest.testInCondtion();
//        xxxTest.listCat();
//        xxxTest.testFindToHandle();
//        xxxTest.testTemporaryTable();
//        xxxTest.inOrder();
//        xxxTest.testOrderFindByAlia();
//        xxxTest.testOrderFind();
//        xxxTest.testNonPaged();
//        xxxTest.testOrderFindByAlia();
//        xxxTest.testListWithEnum();
//        xxxTest.testResultMapSimpleSource();
//        xxxTest.testListPlainValue();
//        xxxTest.testAlia();
//        xxxTest.resultKeyFuntion();
//        xxxTest.testSimple();
//        xxxTest.testCriteria();
//        xxxTest.testOne();
//        xxxTest.testCreate();
        xxxTest.refreshByCondition();
//        xxxTest.testRemove();
//        xxxTest.createBatch();
//        xxxTest.removeRefreshCreate();
//        xxxTest.testRefreshConditionRemote();
//        xxxTest.testCriteriaRemote();

//        xxxTest.testResultMappedRemote();

//        xxxTest.testCreate();
//        xxxTest.testSimple();
    }

}
