package x7;

import io.xream.sqli.builder.*;
import io.xream.sqli.test.SqlGenerator;
import org.junit.Test;
import x7.demo.entity.Cat;
import x7.demo.entity.Order;
import x7.demo.entity.OrderItem;
import x7.demo.entity.OrderLog;

import java.util.Arrays;

/**
 * @author Sim
 */
public class CriterialToSqlTester {


    @Test
    public void testWithOfSub(){

        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder.distinct("o.id");
        builder.beginSub().eq("o.name",null).endSub();
        builder.in("i.name", Arrays.asList("test"));
        builder.nonNull("i.name").nonNull("l.log");
        builder.sourceBuilder().source("order").alia("o");
        builder.sourceBuilder().source("orderItem").alia("i").join(JoinType.INNER_JOIN)
                .on("orderId", JoinFrom.of("o","id"));
        builder.sourceBuilder().with(//demo for clickhouse
                subBuilder -> { // sub0
                    subBuilder.resultKey("ol.orderId", "orderId")
                            .resultKey("ol.log","log").gt("ol.orderId",1).groupBy("ol.orderId").groupBy("ol.log");
                    subBuilder.sourceBuilder().with(
                            subBuilder1 -> { //sub1
                                subBuilder1.resultKey("ot.orderId","orderId")
                                        .resultKey("ot.log","log")
                                        .sourceScript("FROM orderLog ot ")
                                        .groupBy("ot.orderId").groupBy("ot.log");
                            } ).alia("ol");
                }).alia("l")
                .join("ANY LEFT JOIN")
                .on("orderId", JoinFrom.of("o","id"));

        builder.groupBy("o.id").sort("o.id", Direction.DESC);
        builder.paged().page(1).rows(10);

        Criteria.ResultMapCriteria criteria = builder.build();

        SqlGenerator.generator()
                .source(Order.class)
                .source(OrderItem.class)
                .source(OrderLog.class)
                .build("testWithOfSub",criteria)
                .generate("CriterialToSqlTester");

    }

    @Test
    public void testSub(){

        CriteriaBuilder.ResultMapBuilder resultMapBuilder = CriteriaBuilder.resultMapBuilder();
        resultMapBuilder.resultKey("c.id","id").resultKey("c.type","type");
        resultMapBuilder.sourceBuilder().sub(subBuilder -> {
            subBuilder.resultKey("id").resultKey("type","type");
            subBuilder.sourceScript("cat");
        }).alia("c");
        Criteria.ResultMapCriteria resultMapCriteria = resultMapBuilder.build();

        SqlGenerator.generator().source(Cat.class).build("testSub",resultMapCriteria).generate("CriterialToSqlTester");

    }
}
