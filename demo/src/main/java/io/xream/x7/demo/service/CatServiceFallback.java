package io.xream.x7.demo.service;

import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.common.util.JsonX;
import io.xream.x7.demo.bean.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatServiceFallback {

    private Logger logger = LoggerFactory.getLogger(CatServiceFallback.class);
    public void refresh(RefreshCondition<Cat> refreshCondition) {
        logger.info(JsonX.toJson(refreshCondition));
    }
}
