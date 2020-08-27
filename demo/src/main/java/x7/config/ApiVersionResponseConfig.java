package x7.config;

import io.xream.x7.reyc.api.SimpleRestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Sim
 */
@Configuration
public class ApiVersionResponseConfig {

    @Bean
    public ViewEntityResponseInterceptor viewEntityResponseInterceptor(SimpleRestTemplate simpleRestTemplate){
        ViewEntityResponseInterceptor interceptor = new ViewEntityResponseInterceptor();
        simpleRestTemplate.add(interceptor);
        return interceptor;
    }
}
