package x7.demo.repository;

import io.xream.sqli.repository.api.BaseRepository;
import org.springframework.stereotype.Repository;
import x7.demo.entity.Order;

@Repository
public interface OrderRepository extends BaseRepository<Order> {
}
