package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import x7.demo.bean.Cat;
import org.springframework.stereotype.Repository;

@Repository
public interface CatRepository extends BaseRepository<Cat> {

}
