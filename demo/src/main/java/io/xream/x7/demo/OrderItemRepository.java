package io.xream.x7.demo;

import io.xream.x7.demo.bean.OrderItem;
import io.xream.x7.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem> {
}
