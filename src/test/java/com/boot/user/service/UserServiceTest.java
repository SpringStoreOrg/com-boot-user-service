package com.boot.user.service;


import com.boot.user.dto.UserDTO;
import com.boot.user.model.ConfirmationToken;
import com.boot.user.model.User;
import com.boot.user.repository.ConfirmationTokenRepository;
import com.boot.user.repository.PasswordResetTokenRepository;
import com.boot.user.repository.UserRepository;
import com.boot.user.validator.TokenValidator;
import com.boot.user.validator.UserValidator;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidator userValidator;

    @Mock
    private TokenValidator tokenValidator;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private EmailService emailSenderService;

    @Mock
    private PasswordResetTokenRepository passwordReserTokenRepository;

    @Captor
    private ArgumentCaptor<ConfirmationToken> confirmationTokenCaptor;

    @Test
    public void addUser() {

        when(passwordEncoder.encode(getUserDTO().getPassword())).thenReturn("testPassword");
        when(userRepository.save(getUser())).thenReturn(getUser());
        when(confirmationTokenRepository.save(any())).thenReturn(any(ConfirmationToken.class));

        UserDTO savedUser = userService.addUser(getUserDTO());

        verify(userRepository).save(getUser());

        verify(confirmationTokenRepository).save(confirmationTokenCaptor.capture());

        ConfirmationToken confirmationToken = confirmationTokenCaptor.getValue();

        verify(confirmationTokenRepository).save(confirmationToken);
        verify(emailSenderService).sendConfirmationEmail(getUser(), confirmationToken);

        Assert.assertEquals(getUserDTO(),savedUser);

        Assert.assertNotNull(confirmationToken);
        Assert.assertEquals(getUser(), confirmationToken.getUser());
        Assert.assertEquals(String.class, confirmationToken.getConfirmationToken().getClass());
        Assert.assertEquals(Date.class, confirmationToken.getCreatedDate().getClass());


    }

    private UserDTO getUserDTO() {
        UserDTO userDTO = new UserDTO();

        userDTO.setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setPassword("testPassword")
                .setEmail("jon278@gaailer.site")
                .setRole("USER")
                .setDeliveryAddress("stret, no. 1");

        return userDTO;
    }

    private User getUser() {
        User user = new User();

        user.setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setPassword("testPassword")
                .setEmail("jon278@gaailer.site")
                .setRole("USER")
                .setCreatedOn(LocalDate.now())
                .setDeliveryAddress("stret, no. 1");

        return user;
    }

}
