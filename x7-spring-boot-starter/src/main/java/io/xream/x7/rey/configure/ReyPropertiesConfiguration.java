package io.xream.x7.rey.configure;

import io.xream.rey.config.ReyConfigurable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.Resource;

/**
 * @author Sim
 */
@EnableConfigurationProperties({ReyProperties.class})
public class ReyPropertiesConfiguration implements ReyConfigurable {

    @Resource
    private ReyProperties reyProperties;

    @Override
    public boolean isCircuitbreakerEnabled(String name) {
        return reyProperties.getCircuitbreaker().get(name).isEnabled();
    }
}
