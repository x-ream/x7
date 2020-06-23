package io.xream.x7;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AppTest {

    @Autowired
    private XxxTest xxxTest;
    @Autowired
    private TransformTest transformTest;
    @Autowired
    private CatTest catTest;
    @Autowired
    private CatRepositoryTest repositoryTest;
    @Autowired
    private DarkRepositoryTest darkRepositoryTest;

//    private Executor executor = Executors.newFixedThreadPool(11);

    @Test
    public void  testAll(){

//        xxxTest.testOrder();
//        xxxTest.testNonPaged();
//        xxxTest.testNonPaged();
//        xxxTest.testNonPaged();
//        xxxTest.domain();

//        xxxTest.inOrder();
//        xxxTest.testOrderFind();
        xxxTest.testOrderFindByAlia();

//        xxxTest.testReyClient();
//        xxxTest.testTime();
//        xxxTest.getBase();

//        xxxTest.testCriteria();
//        xxxTest.testCriteria();
//        xxxTest.testCriteria();
        xxxTest.testListCriteria();



//        xxxTest.testRefreshConditionRemote();
//        xxxTest.testCriteriaRemote();
//        xxxTest.testCriteriaRemote();
//        xxxTest.testCriteriaRemote();


//        xxxTest.testResultMappedRemote();
//        xxxTest.testResultMappedRemote();
//        xxxTest.testResultMappedRemote();
//        xxxTest.testResultMappedRemote();

//        xxxTest.testResultMapped();
//        xxxTest.testResultMapped();
//        xxxTest.testResultMapped();


//        repositoryTest.refresh();

//        xxxTest.testListPlainValue();

//        xxxTest.testAlia();
//        xxxTest.testAlia();
//        xxxTest.testAlia();
//        xxxTest.distinct();
//        xxxTest.distinct();
//

//        xxxTest.testOne();
//        xxxTest.testListCriteria();
//        xxxTest.refreshByCondition();
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
//        xxxTest.testList();
//        xxxTest.testList();
//        xxxTest.testList();
        xxxTest.testLock();

//        xxxTest.in();
//        xxxTest.in();
//        xxxTest.in();
//        xxxTest.get();
//        xxxTest.get();
//        xxxTest.get();
//        xxxTest.testOneKeyRemote();
//        xxxTest.testEnum();

//        xxxTest.removeRefreshCreate();
//
//        xxxTest.testCacheGet();

//        xxxTest.testFallbackOnly("test");

//        xxxTest.testTemporaryTable();

    }

}
