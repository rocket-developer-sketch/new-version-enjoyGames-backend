package com.easygame.api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // every API
                //.allowedOrigins("*") // allowed every origins while developing
                .allowedOrigins("http://localhost:8081", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }

//    @Bean
//    public FilterRegistrationBean<XssFilter> filterRegistrationBean() {
//        FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new XssFilter());
//        registrationBean.addUrlPatterns("/*");
//        return registrationBean;
//    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Swagger UI HTML
        registry.addResourceHandler("/easygame-api-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        // Swagger UI JS/CSS
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        // -- Static resources
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
}
