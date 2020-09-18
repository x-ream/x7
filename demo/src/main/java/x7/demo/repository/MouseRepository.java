package x7.demo.repository;

import io.xream.sqli.repository.api.BaseRepository;
import io.xream.sqli.repository.api.ResultMapRepository;
import org.springframework.stereotype.Repository;
import x7.demo.entity.Mouse;

@Repository
public interface MouseRepository extends BaseRepository<Mouse>, ResultMapRepository {
}
