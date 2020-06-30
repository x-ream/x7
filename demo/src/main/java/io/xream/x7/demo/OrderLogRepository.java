package io.xream.x7.demo;

import io.xream.x7.demo.bean.OrderLog;
import io.xream.x7.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLogRepository extends BaseRepository<OrderLog> {
}
