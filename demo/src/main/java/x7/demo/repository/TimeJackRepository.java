package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import io.xream.sqli.api.ResultMapRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeJackRepository extends BaseRepository<TimeJack>, ResultMapRepository {
}

