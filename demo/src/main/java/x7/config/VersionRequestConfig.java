package x7.config;

import io.xream.x7.base.util.LoggerProxy;
import io.xream.x7.reyc.api.HeaderRequestInterceptor;
import io.xream.x7.reyc.api.SimpleRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Author Sim
 */
@Configuration
public class VersionRequestConfig {

    @Bean
    public VersionRequestInterceptor versionRequestInterceptor(SimpleRestTemplate simpleRestTemplate){
        VersionRequestInterceptor interceptor = new VersionRequestInterceptor();
        simpleRestTemplate.add(interceptor);
        return interceptor;
    }

    public class VersionRequestInterceptor implements HeaderRequestInterceptor {

        @Override
        public void handle(Class clzz, Map<String, String> map) {

            String version = map.get("VERSION");
            LoggerProxy.info(clzz, "VERSION="+version);

        }
    }
}
