package x7;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AppTest {

    @Autowired
    private XxxTest xxxTest;

    @Test
    public void testAll(){

//        xxxTest.testFindToHandle();
//        xxxTest.testTemporaryTable();

        xxxTest.orderSharding();
        xxxTest.testNonPaged();


//        xxxTest.inOrder();
//        xxxTest.testOrderFind();
        xxxTest.testOrderFindByAlia();
        xxxTest.testNonPaged();
//
        xxxTest.testCriteria();
//        xxxTest.testCriteria();
//        xxxTest.testCriteria();

        xxxTest.testListCriteria();

//        xxxTest.testRefreshConditionRemote();
//        xxxTest.testCriteriaRemote();

//        xxxTest.testResultMappedRemote();

//        xxxTest.testResultMapped();

        xxxTest.testListPlainValue();

        xxxTest.testAlia();
        xxxTest.distinct();

//        xxxTest.testOne();
//        xxxTest.testListCriteria();
        xxxTest.refreshByCondition();
//        xxxTest.testListCriteria();
//        xxxTest.testRemove();
//        xxxTest.testListCriteria();
//        xxxTest.testCreate();
//        xxxTest.testListCriteria();
//        xxxTest.testCreateOrReplace();
//        xxxTest.testListCriteria();
//        xxxTest.testListCriteria();
//        xxxTest.create();
//        xxxTest.createBatch();
//        xxxTest.testRemove();
//        xxxTest.testRestTemplate();

//        xxxTest.testLock();

//        xxxTest.testLock();


    }

}
