package io.xream.x7.demo;

import io.xream.x7.demo.bean.Parrot;
import org.springframework.stereotype.Repository;
import io.xream.sqli.api.BaseRepository;

@Repository
public interface ParrotRepository extends BaseRepository<Parrot> {

}
