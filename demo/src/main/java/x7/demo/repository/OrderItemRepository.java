package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import org.springframework.stereotype.Repository;
import x7.demo.entity.OrderItem;

@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem> {
}
