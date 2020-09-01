package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import io.xream.sqli.api.ResultMapRepository;
import org.springframework.stereotype.Repository;
import x7.demo.entity.CatMouse;

@Repository
public interface CatMouseRepository extends BaseRepository<CatMouse>, ResultMapRepository {
}
