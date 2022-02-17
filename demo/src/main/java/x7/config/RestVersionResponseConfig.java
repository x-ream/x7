package x7.config;

import io.xream.x7.base.KV;
import io.xream.x7.reyc.api.ClientHeaderInterceptor;
import io.xream.x7.reyc.api.SimpleRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sim
 */
@Configuration
public class RestVersionResponseConfig {

    @Value("project.version")
    private String version;

    @Bean
    public VersionClientHeaderInterceptor versionRequestInterceptor(SimpleRestTemplate simpleRestTemplate){
        VersionClientHeaderInterceptor interceptor = new VersionClientHeaderInterceptor();
        simpleRestTemplate.headerInterceptor(interceptor);
        return interceptor;
    }

    public class VersionClientHeaderInterceptor implements ClientHeaderInterceptor {

        @Override
        public KV apply() {
            return new KV("VERSION", version);
        }
    }
}
