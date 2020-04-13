package io.xream.x7.common.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;


public class CorsConfig  {

    @ConditionalOnMissingBean(CorsFilter.class)
    @Bean
    public CorsFilter corsFilter(){
        CorsFilter corsFilter =  new CorsFilter();
        return corsFilter;
    }

    @Bean
    public FilterRegistrationBean corsFilterRegistration(CorsFilter corsFilter) {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(corsFilter);
        registration.addUrlPatterns("/*");
        registration.setName(CorsFilter.class.getSimpleName());
        registration.setOrder(1);
        return registration;
    }
}