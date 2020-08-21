package x7.demo.repository;

import io.xream.sqli.api.BaseRepository;
import x7.demo.bean.OrderItem;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem> {
}
