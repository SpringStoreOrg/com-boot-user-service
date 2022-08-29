package com.boot.user.service;


import com.boot.user.dto.UserDTO;
import com.boot.user.model.User;
import com.boot.user.repository.ConfirmationTokenRepository;
import com.boot.user.repository.PasswordResetTokenRepository;
import com.boot.user.repository.UserRepository;
import com.boot.user.validator.TokenValidator;
import com.boot.user.validator.UserValidator;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static com.boot.user.model.User.dtoToUserEntity;
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



    @Test
    public void addUser(){
        User user = dtoToUserEntity(getUserDTO());
             user.setCreatedOn(LocalDate.now());

        when(passwordEncoder.encode(getUserDTO().getPassword())).thenReturn("testPassword");

        when(userRepository.save(any())).thenReturn(user);

        UserDTO returnedUser =   userService.addUser(getUserDTO());

        verify(userRepository, times(1)).save(user);

        Assert.assertEquals(returnedUser,getUserDTO());
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

}
