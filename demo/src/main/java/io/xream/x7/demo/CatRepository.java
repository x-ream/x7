package io.xream.x7.demo;

import io.xream.x7.demo.bean.Cat;
import org.springframework.stereotype.Repository;
import x7.repository.BaseRepository;

@Repository
public interface CatRepository extends BaseRepository<Cat> {

}
