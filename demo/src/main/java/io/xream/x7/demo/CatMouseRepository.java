package io.xream.x7.demo;

import io.xream.x7.demo.bean.CatMouse;
import org.springframework.stereotype.Repository;
import io.xream.sqli.api.BaseRepository;

@Repository
public interface CatMouseRepository extends BaseRepository<CatMouse> {
}
