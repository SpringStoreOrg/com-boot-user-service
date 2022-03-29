package com.boot.user.config;

import java.io.IOException;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.velocity.VelocityEngineFactory;
import org.springframework.web.client.RestTemplate;

import com.boot.user.client.CartServiceClient;
import com.boot.user.client.ProductServiceClient;

@Configuration
public class AppConfig {



	@Bean
	public RestTemplate template() {
		return new RestTemplate();
	}

	@Bean
	public ProductServiceClient productServiceClient() {
		return new ProductServiceClient();
	}

	@Bean
	public CartServiceClient cartServiceClient() {
		return new CartServiceClient();
	}
	
	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		mailSender.setHost("smtp.hostinger.com");
		mailSender.setPort(587);

		mailSender.setUsername("noreply@springwebstore.com");
		mailSender.setPassword("QQQwwwEEE1234$");

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.starttls.enable", "true");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.transport.protocol", "smtp");
        javaMailProperties.put("mail.debug", "true");
 
        mailSender.setJavaMailProperties(javaMailProperties);

		return mailSender;
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
}
