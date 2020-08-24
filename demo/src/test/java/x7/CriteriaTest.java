package x7;

import io.xream.sqli.builder.BuildingBlock;
import io.xream.sqli.builder.Criteria;
import io.xream.x7.base.util.JsonX;

public class CriteriaTest {
    public static void main(String[] args) {
        BuildingBlock buildingBlock = new BuildingBlock();
        buildingBlock.setKey(" != ");
        Criteria criteria = new Criteria();
        criteria.getBuildingBlockList().add(buildingBlock);

        String json = JsonX.toJson(criteria);
        Criteria c = JsonX.toObject(json, Criteria.class);

        System.out.println(c);

    }
}
