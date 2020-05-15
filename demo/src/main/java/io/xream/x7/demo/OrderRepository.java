package io.xream.x7.demo;

import io.xream.x7.demo.bean.Order;
import io.xream.x7.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends BaseRepository<Order> {
}
