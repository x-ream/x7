package io.xream.x7.demo.controller;

import io.xream.x7.common.bean.*;
import io.xream.x7.common.bean.condition.InCondition;
import io.xream.x7.common.web.Direction;
import io.xream.x7.common.web.Page;
import io.xream.x7.common.web.ViewEntity;
import io.xream.x7.demo.OrderItemRepository;
import io.xream.x7.demo.OrderRepository;
import io.xream.x7.demo.bean.Order;
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
        builder.distinct("order.id");
        builder.resultKeyFunction(FunctionAlia.wrap("order","at"),"YEAR(?)","order.createAt");
        builder.and().eq("order.name","test");
//        builder.and().eq("orderItem.name","test");
//        builder.and().nonNull("orderItem.name");
//        builder.and().in("orderItem.name", Arrays.asList("xxx"));
        builder.and().beginSub().gt("order.createAt",System.currentTimeMillis() - 1000000)
                .and().lt("order.createAt",System.currentTimeMillis()).endSub();
        builder.sourceScript("from order inner join orderItem on orderItem.orderId = order.id and orderItem.name = order.name");
        builder.paged().ignoreTotalRows().page(1).rows(10).sort("order.id", Direction.DESC);


        Criteria.ResultMappedCriteria criteria = builder.get();

        Page<Map<String,Object>> page = this.orderRepository.find(criteria);

        return ViewEntity.ok(page);
    }


    @RequestMapping("/findByAlia")
    public ViewEntity findBuAlia(){
        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.distinct("o.id");
        builder.and().eq("o.name","test");
        builder.and().in("i.name", Arrays.asList("test"));
        builder.and().nonNull("i.name");
        builder.and().nonNull("l.log");
//        builder.and().beginSub().gt("o.createAt",System.currentTimeMillis() - 1000000)
//                .and().lt("o.createAt",System.currentTimeMillis()).endSub();
//        builder.sourceScript("from order o INNER join orderItem i ON i.orderId != o.id AND i.name = o.name");

        builder.sourceScript().source("order").alia("o");
        builder.sourceScript().source("orderItem").alia("i").joinType(JoinType.INNER_JOIN)
                .on("orderId", On.Op.NE,JoinFrom.wrap("o","id"))
                .onOr("name",On.Op.EQ, JoinFrom.wrap("o","name"));
        builder.sourceScript().source("orderLog").alia("l").joinType(JoinType.LEFT_JOIN)
                .on("orderId", On.Op.EQ,JoinFrom.wrap("o","id"));

        builder.paged().ignoreTotalRows().page(1).rows(10).sort("o.id", Direction.DESC);

        Criteria.ResultMappedCriteria criteria = builder.get();

        Page<Map<String,Object>> page = this.orderRepository.find(criteria);

        return ViewEntity.ok(page);
    }

    public ViewEntity in(){
        InCondition inCondition = new InCondition();
        inCondition.setProperty("name");
        inCondition.setInList(Arrays.asList("xxx"));
        List<Order> list = this.orderRepository.in(inCondition);
        return ViewEntity.ok(list);
    }

}
