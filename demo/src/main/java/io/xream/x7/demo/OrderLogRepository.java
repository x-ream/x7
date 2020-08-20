package io.xream.x7.demo;

import io.xream.sqli.api.BaseRepository;
import io.xream.x7.demo.bean.OrderLog;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLogRepository extends BaseRepository<OrderLog> {
}
