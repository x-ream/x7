package x7.reyc;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.servlet.BraveServletFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

public class TracingServletRegistrary {

    @ConditionalOnMissingBean(BraveServletFilter.class)
    @ConditionalOnBean(Brave.class)
    @Bean
    public BraveServletFilter braveServletFilter(Brave brave) {
        return new BraveServletFilter(brave.serverRequestInterceptor(), brave.serverResponseInterceptor(),
                new DefaultSpanNameProvider());
    }

    @Bean
    public FilterRegistrationBean braveServletFilterRegistration(BraveServletFilter braveServletFilter) {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(braveServletFilter);
        registration.addUrlPatterns("/*");
        registration.setName(BraveServletFilter.class.getSimpleName());
        registration.setOrder(10);
        return registration;
    }
}
