package x7.demo.controller;

import io.xream.sqli.builder.*;
import io.xream.sqli.page.Page;
import io.xream.x7.base.web.ViewEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import x7.demo.entity.Order;
import x7.demo.entity.OrderType;
import x7.demo.repository.OmsRepository;
import x7.demo.repository.OrderRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Value("${spring.profiles.active:dev}")
    private String profile;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OmsRepository omsRepository;

    @RequestMapping("/create")
    public boolean create(@RequestBody Order order) {

        return this.orderRepository.create(order);

    }

    @RequestMapping("/sharding")
    public Order sharding(){

        CriteriaBuilder builder = CriteriaBuilder.builder(Order.class);
        builder.eq("userId", 5);
        Order order = this.orderRepository.list(builder.build()).get(0);

        return order;

    }

    @RequestMapping("/find")
    public ViewEntity find(){
        System.out.println(profile);
        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder.resultWithDottedKey().distinct("o.id");
        builder.resultKeyFunction(ResultKeyAlia.of("o","cl"),"CHAR_LENGTH(?)","RUNOOB");
        builder.resultKeyFunction(ResultKeyAlia.of("o","at"),"YEAR(o.createAt)");
        builder.resultKeyFunction(ResultKeyAlia.of("o","xxx"),"CASE WHEN ISNULL(o.name) THEN 0 ELSE YEAR(o.createAt) END");
//        builder.eq("o.name","test");
//        builder.and().eq("i.name","test");
//        builder.and().nonNull("l.log");
//        builder.and().nonNull("i.name");
//        builder.and().in("i.name", Arrays.asList("xxx"));
//        builder.and().beginSub().gt("o.createAt",System.currentTimeMillis() - 1000000)
//                .and().lt("o.createAt",System.currentTimeMillis()).endSub();
//        builder.sourceScript().source("order").alia("o");
//        builder.sourceScript().source("orderItem").join(JoinType.INNER_JOIN).on("orderId",JoinFrom.of("order","id"))
//                .more().x("orderItem.name = order.name");
        builder.sourceScript("FROM order o INNER JOIN orderItem i ON o.id = i.orderId" +
                " INNER JOIN orderLog l ON o.id = l.orderId");
        builder.withoutOptimization();
        builder.sort("o.id", Direction.DESC);
        builder.paged().ignoreTotalRows().page(1).rows(10);


        Criteria.ResultMapCriteria criteria = builder.build();

        Page<Map<String,Object>> page = this.omsRepository.find(criteria);

        return ViewEntity.ok(page);
    }


    @RequestMapping("/findByAlia")
    public ViewEntity findBuAlia(){
        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
//        builder.resultKey("o.name","o_name").distinct("o.id").reduce(ReduceType.SUM,"i.quantity", Having.of(Op.LT,10));
//        builder.resultWithDottedKey();
        builder.resultKey("o.name");
        builder.beginSub().eq("o.name",null).endSub();
        builder.in("i.name", Arrays.asList("test"));
        builder.nonNull("i.name").nonNull("l.log");
        builder.sourceBuilder().source("order").alia("o");
        builder.sourceBuilder().source("orderItem").alia("i").join(JoinType.LEFT_JOIN)
                .on("orderId", JoinFrom.of("o","id"))
                .more().or()
                    .beginSub()
                        .x("i.orderId > ? and YEAR(o.createAt) >= ?", 2,2020).or().lte("i.orderId",2)
                            .beginSub().eq("i.type", OrderType.SINGLE).endSub()
                        .or().eq("i.type", null)
                            .beginSub().eq("o.type",OrderType.SINGLE).endSub().or()
                    .endSub().x("i.orderId > 1");

        builder.sourceBuilder().sub(
                subBuilder -> {
                    subBuilder
                            .resultKey("ol.orderId", "orderId")
                            .resultKey("ol.log","log").gt("ol.orderId",1).groupBy("ol.orderId");
                    subBuilder.sourceBuilder().sub(subBuilder1 -> {
                                subBuilder1.resultKey("ot.orderId","orderId")
                                        .resultKey("ot.log","log")
                                        .sourceScript("FROM orderLog ot INNER JOIN cat c ON c.id = ot.id")
                                        .withoutOptimization()
                                        .sort("ot.id",Direction.ASC);
                            } ).alia("ol");
                }
        ).alia("l").join(JoinType.LEFT_JOIN).on("orderId",JoinFrom.of("o","id"));

        builder.groupBy("o.id").sort("o.id", Direction.DESC);
        builder.paged().page(1).rows(10);

        Criteria.ResultMapCriteria criteria = builder.build();

        Page<Map<String,Object>> page = this.omsRepository.find(criteria);

        return ViewEntity.ok(page);
    }

    public ViewEntity in(){
        List<Order> list = this.orderRepository.in(
                "name",Arrays.asList("xxx")
        );
        return ViewEntity.ok(list);
    }

    @RequestMapping("/verify")
    public boolean verify(){
        return true;
    }
}
