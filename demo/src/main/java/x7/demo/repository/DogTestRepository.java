package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import x7.demo.bean.DogTest;
import org.springframework.stereotype.Repository;

@Repository
public interface DogTestRepository extends BaseRepository<DogTest> {

}
