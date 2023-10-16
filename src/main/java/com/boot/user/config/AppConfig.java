package com.boot.user.config;

import com.boot.user.client.RetrieveMessageErrorDecoder;
import com.boot.user.dto.AddressDTO;
import com.boot.user.dto.CreateUserDTO;
import com.boot.user.dto.GetUserDTO;
import com.boot.user.model.Address;
import com.boot.user.model.User;
import feign.codec.ErrorDecoder;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.velocity.VelocityEngineFactory;

import java.io.IOException;
import java.util.Properties;


@Configuration
public class AppConfig {
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

    @Bean
    public ErrorDecoder errorDecoder() {
        return new RetrieveMessageErrorDecoder();
    }

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(User.class, CreateUserDTO.class);
        modelMapper.typeMap(User.class, GetUserDTO.class);
        modelMapper.typeMap(Address.class, AddressDTO.class);
        modelMapper.addMappings(new PropertyMap<Address, Address>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getCreatedOn());
                skip(destination.getLastUpdatedOn());
                skip(destination.getUser());
            }
        });

        return modelMapper;
    }
}
