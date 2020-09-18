package x7;

import io.xream.sqli.api.NativeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * @Author Sim
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class NativeRepositoryTester {

    @Autowired
    private NativeRepository nativeRepository;
    @Test
    public void test(){

        String query = "select * from t_cat";

        List<Map<String,Object>> list = this.nativeRepository.list(query, null);
        System.out.println(list);

    }
}
