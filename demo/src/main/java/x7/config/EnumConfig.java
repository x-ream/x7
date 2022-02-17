package x7.config;

import io.xream.sqli.api.customizer.EnumSupportCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sim
 */
@Configuration
public class EnumConfig {
    @Bean
    public EnumSupportCustomizer enumSupportCustomizer(){
        return () -> new MyEnumSupport();
    }
}
