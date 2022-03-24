package x7;

import io.xream.sqli.builder.Criteria;
import io.xream.sqli.builder.CriteriaBuilder;
import io.xream.sqli.converter.ResultMapToBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import x7.demo.entity.Cat;
import x7.demo.service.CatService;

import java.util.List;
import java.util.Map;

/**
 * @author Sim
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ResultMapToBeanTester {

    @Autowired
    private CatService catService;
    
    @Test
    public void testEnumMap() {

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder.resultKey("testBoo").resultKey("dogId").resultKey("createAt");
        builder.sourceScript("FROM cat");
        Criteria.ResultMapCriteria criteria = builder.build();
        List<Map<String, Object>> mapList = this.catService.listByResultMap(criteria);

        for (Map<String, Object> map : mapList) {
            System.out.println(map);
            Cat cat = ResultMapToBean.copy(Cat.class, map);
            System.out.println("___TEST BOO:" + cat.getTestBoo() + ",dogId: " + cat.getDogId()
                    + ", creatAt: " + cat.getCreateAt()
            );
        }
    }
}
