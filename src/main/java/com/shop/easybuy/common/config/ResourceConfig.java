package com.shop.easybuy.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.time.Duration;

@Configuration
public class ResourceConfig implements WebFluxConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/db/item.images/")
                .setCacheControl(CacheControl.maxAge(Duration.ofMinutes(5)));
    }
}
