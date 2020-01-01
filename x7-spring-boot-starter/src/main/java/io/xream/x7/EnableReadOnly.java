package io.xream.x7;

import org.springframework.context.annotation.Import;
import io.xream.x7.repository.config.datasource.DataSourceAspect;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({DataSourceAspect.class})
public @interface EnableReadOnly {
}
