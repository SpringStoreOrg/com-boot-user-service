package com.boot.user.service;


import com.boot.user.config.AppConfig;
import com.boot.user.model.ConfirmationToken;
import com.boot.user.model.User;
import com.boot.user.model.UserFavorite;
import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
 class EmailServiceTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    AppConfig appConfig;

    @Mock
    JavaMailSender emailSender;

    @Mock
    VelocityEngine velocityEngine;

    @Mock
    MimeMessage mimeMessage;

    @Value("${user.service.url}")
    public String userServiceUrl;


    @Test
     void sendConfirmationEmail() throws MessagingException {
        User user =  getUser();
        ConfirmationToken confirmationToken =  getToken();
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(appConfig.userServiceRestTemplateUrl()).thenReturn(new RestTemplateBuilder().rootUri(userServiceUrl).build());
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendConfirmationEmail(user, confirmationToken);

        assertNotNull(confirmationToken);
        verify(mimeMessage).setSubject("SpringStore confirmation Email");
    }

    private UserFavorite createUserFavorite(User user, String productName){

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUser(user);
        userFavorite.setProductName(productName);

        return userFavorite;
    }

    private User getUser() {
        User user = new User();
        List<UserFavorite> userFavorites = new ArrayList<>();
        List<String> productNamesList = new ArrayList<>(Arrays.asList("testProductName1,testProductName2".split(",")));

        productNamesList.forEach(product ->userFavorites.add(createUserFavorite(user,product)));

        user.setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setPassword("testPassword")
                .setEmail("jon278@gaailer.site")
                .setRole("USER")
                .setActivated(true)
                .setCreatedOn(LocalDate.now())
                .setUserFavorites(userFavorites)
                .setDeliveryAddress("street, no. 1");

        return user;
    }

    private ConfirmationToken getToken() {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setGregorianChange(new Date(Long.MIN_VALUE));
        Date dateToConvert = calendar.getTime();

        ConfirmationToken token = new ConfirmationToken();
        token.setTokenId(2);
        token.setToken("ec9f508e-2063-4057-840f-efce2d1bbae5");
        token.setUser(getUser());
        token.setCreatedDate(dateToConvert);
        return token;
    }

}