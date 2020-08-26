package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import org.springframework.stereotype.Repository;
import x7.demo.bean.Parrot;

@Repository
public interface ParrotRepository extends BaseRepository<Parrot> {

}
