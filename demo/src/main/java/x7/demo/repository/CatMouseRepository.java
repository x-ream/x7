package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import x7.demo.bean.CatMouse;
import org.springframework.stereotype.Repository;

@Repository
public interface CatMouseRepository extends BaseRepository<CatMouse> {
}
