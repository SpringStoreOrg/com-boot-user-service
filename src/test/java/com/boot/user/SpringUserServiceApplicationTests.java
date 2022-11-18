package com.boot.user;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootTest(classes = SpringUserServiceApplicationTests.class)
@EnableWebMvc
public class SpringUserServiceApplicationTests {

	@Test
	public void contextLoads() {
	}

}
