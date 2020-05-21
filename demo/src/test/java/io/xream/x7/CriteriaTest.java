package io.xream.x7;

import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.X;
import io.xream.x7.common.util.JsonX;

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
