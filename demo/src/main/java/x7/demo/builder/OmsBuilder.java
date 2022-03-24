package x7.demo.builder;

import io.xream.sqli.builder.*;
import x7.demo.ro.OrderFindDto;

import java.util.Arrays;

/**
 * @author Sim
 */
public class OmsBuilder {

    public Criteria.ResultMapCriteria orderDtoBuilder(OrderFindDto orderFindDto){
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

        Criteria.ResultMapCriteria resultMapCriteria = builder.build();
        return resultMapCriteria;
    }

    public Criteria.ResultMapCriteria testSubBuilder(){
        CriteriaBuilder.ResultMapBuilder resultMapBuilder = CriteriaBuilder.resultMapBuilder();
        resultMapBuilder.resultKey("c.id","id").resultKey("c.type","type");
        resultMapBuilder.sourceBuilder().sub(subBuilder -> {
            subBuilder.resultKey("id").resultKey("type","type");
            subBuilder.sourceScript("cat");
        }).alia("c");
        Criteria.ResultMapCriteria resultMapCriteria = resultMapBuilder.build();
        return resultMapCriteria;
    }
}
