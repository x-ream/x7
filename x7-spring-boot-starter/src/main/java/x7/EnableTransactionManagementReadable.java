package x7;

import org.springframework.context.annotation.Import;
import x7.repository.config.datasource.DataSourceAspect;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({DataSourceAspect.class})
public @interface EnableTransactionManagementReadable {
}
