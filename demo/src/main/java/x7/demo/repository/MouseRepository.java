package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import x7.demo.bean.Mouse;
import org.springframework.stereotype.Repository;

@Repository
public interface MouseRepository extends BaseRepository<Mouse> {
}
