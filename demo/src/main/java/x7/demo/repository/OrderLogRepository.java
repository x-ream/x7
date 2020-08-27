package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import org.springframework.stereotype.Repository;
import x7.demo.entity.OrderLog;

@Repository
public interface OrderLogRepository extends BaseRepository<OrderLog> {
}
