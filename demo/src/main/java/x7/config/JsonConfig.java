package x7.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.xream.sqli.util.SqliJsonUtil;
import org.springframework.context.annotation.Bean;

/**
 * @Author Sim
 */
//@Configuration
public class JsonConfig {

    @Bean
    public SqliJsonUtil.Customizer sqliJsonUtilCustomizer(ObjectMapper objectMapper){
        System.out.println("_________"+objectMapper);
        return new SqliJsonUtil.Customizer() {
            @Override
            public ObjectMapper customize() {
                return objectMapper;
            }
        };
    }
}
