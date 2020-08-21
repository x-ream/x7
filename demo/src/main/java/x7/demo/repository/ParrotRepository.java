package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import x7.demo.bean.Parrot;
import org.springframework.stereotype.Repository;

@Repository
public interface ParrotRepository extends BaseRepository<Parrot> {

}
