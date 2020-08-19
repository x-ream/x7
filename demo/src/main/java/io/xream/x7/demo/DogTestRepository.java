package io.xream.x7.demo;

import io.xream.x7.demo.bean.DogTest;
import org.springframework.stereotype.Repository;
import io.xream.sqli.api.BaseRepository;

@Repository
public interface DogTestRepository extends BaseRepository<DogTest> {

}
