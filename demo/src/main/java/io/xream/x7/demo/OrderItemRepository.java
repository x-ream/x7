package io.xream.x7.demo;

import io.xream.sqli.api.BaseRepository;
import org.springframework.stereotype.Repository;
import io.xream.x7.demo.bean.OrderItem;

@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem> {
}
