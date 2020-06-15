package io.xream.x7.demo.service;

import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.demo.bean.Cat;

public interface CatService {

    boolean refresh(RefreshCondition<Cat> refreshCondition);
}
