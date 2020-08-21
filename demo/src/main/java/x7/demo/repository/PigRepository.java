package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import x7.demo.bean.Pig;
import org.springframework.stereotype.Repository;

@Repository
public interface PigRepository extends BaseRepository<Pig> {
}
