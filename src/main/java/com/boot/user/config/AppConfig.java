package com.boot.user.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.velocity.VelocityEngineFactory;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.Properties;


@Configuration
public class AppConfig {

    @Value("${cart.service.url}")
    private String cartServiceUrl;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${user.service.url}")
    public String userServiceUrl;

    @Bean(name = "cartServiceRestTemplate")
    public RestTemplate cartServiceRestTemplateUrl() {
        return new RestTemplateBuilder().rootUri(cartServiceUrl).build();
    }

    @Bean(name = "productServiceRestTemplate")
    public RestTemplate productServiceRestTemplateUrl() {
        return new RestTemplateBuilder().rootUri(productServiceUrl).build();
    }

    @Bean(name = "userServiceRestTemplate")
    public RestTemplate userServiceRestTemplateUrl() {
        return new RestTemplateBuilder().rootUri(userServiceUrl).build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public VelocityEngine getVelocityEngine() throws VelocityException, IOException {
        VelocityEngineFactory velocityEngineFactory = new VelocityEngineFactory();
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        velocityEngineFactory.setVelocityProperties(props);
        return velocityEngineFactory.createVelocityEngine();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("User Application API").description(
                        "This is Spring Boot RESTful service using springdoc-openapi and OpenAPI 3."));
    }
}
