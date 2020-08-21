package x7;

import io.xream.sqli.core.builder.Criteria;
import io.xream.sqli.core.builder.X;
import io.xream.x7.base.util.JsonX;

public class CriteriaTest {
    public static void main(String[] args) {
        X x = new X();
        x.setKey(" != ");
        Criteria criteria = new Criteria();
        criteria.getListX().add(x);

        String json = JsonX.toJson(criteria);
        Criteria c = JsonX.toObject(json, Criteria.class);

        System.out.println(c);

    }
}
