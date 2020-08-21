package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import org.springframework.stereotype.Repository;
import x7.demo.bean.CatTest;

@Repository
public interface CatTestRepository extends BaseRepository<CatTest> {

}
