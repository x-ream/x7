package x7.demo.test;

import io.xream.sqli.annotation.GenSql;
import io.xream.sqli.builder.Criteria;
import x7.demo.builder.OmsBuilder;
import x7.demo.ro.OrderFindDto;

/**
 * @Author Sim
 */
@GenSql
public class OmsBuilderToSql {

    private OmsBuilder omsBuilder = new OmsBuilder();

    public Criteria.ResultMapCriteria orderDtoBuilder(){
        OrderFindDto dto = new OrderFindDto();
        return omsBuilder.orderDtoBuilder(dto);
    }

    public Criteria.ResultMapCriteria testSubBuilder(){
        return omsBuilder.testSubBuilder();
    }
}
