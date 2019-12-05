package io.xream.x7.demo;

import io.xream.x7.demo.bean.TransformedDemo;
import org.springframework.stereotype.Repository;
import x7.repository.BaseRepository;

@Repository
public interface TransformedDemoRepository extends BaseRepository<TransformedDemo> {
}
