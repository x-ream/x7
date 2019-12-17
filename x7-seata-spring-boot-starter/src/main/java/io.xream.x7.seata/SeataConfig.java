package io.xream.x7.seata;

import io.xream.x7.reyc.api.SimpleRestTemplate;
import org.springframework.context.annotation.Bean;

public class SeataConfig {

    @Bean
    public SeataInterceptor seataInterceptor(SimpleRestTemplate simpleRestTemplate){
        SeataInterceptor seataInterceptor = new SeataInterceptor();
        simpleRestTemplate.add(seataInterceptor);
        return seataInterceptor;
    }
}
