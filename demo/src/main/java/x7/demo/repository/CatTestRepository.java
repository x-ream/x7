package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import io.xream.sqli.api.ResultMapRepository;
import org.springframework.stereotype.Repository;
import x7.demo.entity.CatTest;

@Repository
public interface CatTestRepository extends BaseRepository<CatTest>, ResultMapRepository {

}
