package x7.config;


import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Date;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        Jackson2ObjectMapperBuilderCustomizer cunstomizer = new Jackson2ObjectMapperBuilderCustomizer() {

            @Override
            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {

                jacksonObjectMapperBuilder.serializerByType(Date.class, new DateToLongSerializer());
            }
        };

        return cunstomizer;
    }
}