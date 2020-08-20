package io.xream.x7.demo;

import io.xream.sqli.api.BaseRepository;
import io.xream.x7.demo.bean.Parrot;
import org.springframework.stereotype.Repository;

@Repository
public interface ParrotRepository extends BaseRepository<Parrot> {

}
