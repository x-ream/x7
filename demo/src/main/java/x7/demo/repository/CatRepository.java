package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import org.springframework.stereotype.Repository;
import x7.demo.bean.Cat;

@Repository
public interface CatRepository extends BaseRepository<Cat> {

}
