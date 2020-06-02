package io.xream.x7.demo.controller;

import io.xream.x7.common.bean.*;
import io.xream.x7.common.bean.condition.InCondition;
import io.xream.x7.common.web.Direction;
import io.xream.x7.common.web.Page;
import io.xream.x7.common.web.ViewEntity;
import io.xream.x7.demo.OrderItemRepository;
import io.xream.x7.demo.OrderRepository;
import io.xream.x7.demo.bean.Order;
import io.xream.x7.demo.bean.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/create")
    public boolean create(@RequestBody Order order) {

        long id = this.orderRepository.create(order);
        return id > 0;
    }

    @RequestMapping("/find")
    public ViewEntity find(){
        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.keyWithPoint().distinct("o.id");
        builder.resultKeyFunction(ResultKeyAlia.wrap("o","at"),"YEAR(?)","o.createAt");
        builder.resultKeyFunction(ResultKeyAlia.wrap("o","xxx"),"CASE WHEN ISNULL(?) THEN 0 ELSE YEAR(?) END","o.name","o.createAt");
        builder.eq("o.name","test");
//        builder.and().eq("orderItem.name","test");
//        builder.and().nonNull("orderItem.name");
//        builder.and().in("orderItem.name", Arrays.asList("xxx"));
//        builder.and().beginSub().gt("createAt",System.currentTimeMillis() - 1000000)
//                .and().lt("createAt",System.currentTimeMillis()).endSub();
        builder.sourceScript().source("order").alia("o");
//        builder.sourceScript().source("orderItem").joinType(JoinType.INNER_JOIN).on("orderId",JoinFrom.wrap("order","id"))
//                .more().x("orderItem.name = order.name");
        builder.paged().ignoreTotalRows().page(1).rows(10).sort("o.id", Direction.DESC);


        Criteria.ResultMappedCriteria criteria = builder.get();

        Page<Map<String,Object>> page = this.orderRepository.find(criteria);

        return ViewEntity.ok(page);
    }


    @RequestMapping("/findByAlia")
    public ViewEntity findBuAlia(){
        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.resultKey("o.name").distinct("o.id").reduce(ReduceType.COUNT,"o.createAt",Having.wrap(Op.GTE,0));
        builder.beginSub().eq("o.name",null).endSub();
        builder.in("i.name", Arrays.asList("test"));
        builder.nonNull("i.name").nonNull("l.log");
        builder.sourceScript().source("order").alia("o");
        builder.sourceScript().source("orderItem").alia("i").joinType(JoinType.LEFT_JOIN)
                .on("orderId", JoinFrom.wrap("o","id"))
                .more().or()
                    .beginSub()
                        .x("i.orderId = 0").or().lte("i.orderId",2)
                            .beginSub().eq("i.type", OrderType.SINGLE).endSub()
                        .or().eq("i.type", null).
                            beginSub().eq("o.type",OrderType.SINGLE).endSub()
                    .endSub();
        builder.sourceScript().source("orderLog").alia("l").joinType(JoinType.INNER_JOIN)
                .on("orderId", JoinFrom.wrap("o","id"));
        builder.groupBy("o.id");

        builder.paged().ignoreTotalRows().page(1).rows(10).sort("o.id", Direction.DESC);

        Criteria.ResultMappedCriteria criteria = builder.get();

        Page<Map<String,Object>> page = this.orderRepository.find(criteria);

        return ViewEntity.ok(page);
    }

    public ViewEntity in(){
        List<Order> list = this.orderRepository.in(
                InCondition.wrap("name",Arrays.asList("xxx"))
        );
        return ViewEntity.ok(list);
    }

}
