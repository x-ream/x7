package x7.demo.service;

import io.xream.internal.util.JsonX;
import io.xream.sqli.builder.RefreshCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import x7.demo.entity.Cat;

@Component
public class CatServiceFallback {

    private Logger logger = LoggerFactory.getLogger(CatServiceFallback.class);

    public void refresh(RefreshCondition<Cat> refreshCondition) {
        logger.info(JsonX.toJson(refreshCondition));
    }
}
