package com.bezkoder.springjwt.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // Map the local folder to a public URL
//        registry.addResourceHandler("/assets/CollectivePp/**")
//                .addResourceLocations("file:///C:/Users/user/Desktop/snarge%20v1/angular-16-jwt-auth-master/src/assets/CollectivePp/");
//    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from this folder
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/Users/user/Desktop/snarge%20v1/angular-16-jwt-auth-master/src/assets/CollectivePp/");

    }
}
