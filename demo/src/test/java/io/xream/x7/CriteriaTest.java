package io.xream.x7;

import x7.core.bean.Criteria;
import x7.core.util.JsonX;

public class CriteriaTest {
    public static void main(String[] args) {
        Criteria.X x = new Criteria.X();
        x.setKey(" != ");
        Criteria criteria = new Criteria();
        criteria.getListX().add(x);

        String json = JsonX.toJson(criteria);
        Criteria c = JsonX.toObject(json, Criteria.class);

        System.out.println(c);

    }
}
