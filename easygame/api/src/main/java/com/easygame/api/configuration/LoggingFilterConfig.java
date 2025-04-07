package com.easygame.api.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingFilterConfig {
    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> loggingFilter() {
        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestResponseLoggingFilter());
        registrationBean.setOrder(1); // 필터 우선순위
        registrationBean.addUrlPatterns("/*");

        // exclude favicon
//        registrationBean.addInitParameter("excludeUrlPatterns", "/favicon.ico");
        return registrationBean;
    }
}
