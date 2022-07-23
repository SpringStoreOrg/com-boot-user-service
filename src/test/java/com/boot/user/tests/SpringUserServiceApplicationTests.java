package com.boot.user.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringUserServiceApplicationTests.class)
@EnableWebMvc
public class SpringUserServiceApplicationTests {

	@Test
	public void contextLoads() {
	}

}
