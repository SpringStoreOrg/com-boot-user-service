package com.boot.user.service;


import com.boot.user.config.AppConfig;
import com.boot.user.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class EmailServiceTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    AppConfig appConfig;

    @Mock
    JavaMailSender emailSender;

    @Value("${user.service.url}")
    public String userServiceUrl;


    @Test
     void testSendConfirmationEmail() throws MessagingException {
        User user =  getUser();
        ConfirmationToken confirmationToken =  getToken();
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendConfirmationEmail(user, confirmationToken);

        assertNotNull(confirmationToken);
        verify(mimeMessage).setSubject("FractalWoodStories confirmare email");
    }

    @Test
    void testSendPasswordResetEmail() throws MessagingException {
        User user =  getUser();
        PasswordResetToken resetToken =  getResetToken();
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendPasswordResetEmail(user, resetToken);

        assertNotNull(resetToken);
        verify(mimeMessage).setSubject("Fractal Wood Stories reseteaza parola");
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
                .setRoleList(Arrays.asList(new Role()))
                .setVerified(true)
                .setCreatedOn(LocalDateTime.now())
                .setUserFavorites(userFavorites);

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


    private PasswordResetToken getResetToken() {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setGregorianChange(new Date(Long.MIN_VALUE));
        Date dateToConvert = calendar.getTime();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setTokenId(3);
        resetToken.setResetToken("ec9f508e-2063-4057-840f-efce2d1bbae5");
        resetToken.setUser(getUser());
        resetToken.setCreatedDate(dateToConvert);
        return resetToken;
    }
}