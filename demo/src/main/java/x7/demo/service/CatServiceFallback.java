package x7.demo.service;

import io.xream.sqli.builder.RefreshCondition;
import io.xream.x7.base.util.JsonX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x7.demo.entity.Cat;

public class CatServiceFallback {

    private Logger logger = LoggerFactory.getLogger(CatServiceFallback.class);
    public void refresh(RefreshCondition<Cat> refreshCondition) {
        logger.info(JsonX.toJson(refreshCondition));
    }
}
