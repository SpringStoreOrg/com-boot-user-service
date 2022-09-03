package com.boot.user.service;


import com.boot.user.dto.UserDTO;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.model.ConfirmationToken;
import com.boot.user.model.User;
import com.boot.user.repository.ConfirmationTokenRepository;
import com.boot.user.repository.PasswordResetTokenRepository;
import com.boot.user.repository.UserRepository;
import com.boot.user.validator.TokenValidator;
import com.boot.user.validator.UserValidator;
import org.junit.jupiter.api.Assertions;
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
import java.util.GregorianCalendar;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        Assertions.assertEquals(getUserDTO(), savedUser);

        Assertions.assertNotNull(confirmationToken);
        Assertions.assertEquals(getUser(), confirmationToken.getUser());
        Assertions.assertEquals(String.class, confirmationToken.getConfirmationToken().getClass());
        Assertions.assertEquals(Date.class, confirmationToken.getCreatedDate().getClass());


    }

    @Test
    public void confirmUserAccount() throws EntityNotFoundException, UnableToModifyDataException {

        ConfirmationToken token = token();

        when(confirmationTokenRepository.findByConfirmationToken(any())).thenReturn(token);
        when(userRepository.getUserByEmail(token.getUser().getEmail())).thenReturn(getUser());

        userService.confirmUserAccount(token.getConfirmationToken());

        verify(userRepository).getUserByEmail(token().getUser().getEmail());
        verify(tokenValidator).checkTokenAvailability(token.getCreatedDate());
        verify(userRepository).save(getUser().setActivated(true));
    }

    @Test
    public void confirmUserAccount_nullToken() {

        when(confirmationTokenRepository.findByConfirmationToken(any())).thenReturn(null);

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                userService.confirmUserAccount(token().getConfirmationToken()));

        Assertions.assertEquals("Token not found!", exception.getMessage());
    }

    @Test
    public void confirmUserAccount_tokenExpired() {

        when(confirmationTokenRepository.findByConfirmationToken(any())).thenReturn(token());
        when(tokenValidator.checkTokenAvailability(any())).thenReturn(true);

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            userService.confirmUserAccount(token().getConfirmationToken()));

        Assertions.assertEquals("Token Expired!", exception.getMessage());
    }

    @Test
    public void confirmUserAccount_userAlreadyConfirmed() {

        when(confirmationTokenRepository.findByConfirmationToken(any())).thenReturn(token());
        when(tokenValidator.checkTokenAvailability(any())).thenReturn(false);
        when(userRepository.getUserByEmail(token().getUser().getEmail())).thenReturn(getUser().setActivated(true));

        UnableToModifyDataException exception = Assertions.assertThrows(UnableToModifyDataException.class, () ->
            userService.confirmUserAccount(token().getConfirmationToken()));

        Assertions.assertEquals("User was already confirmed!", exception.getMessage());
    }

    private ConfirmationToken token() {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setGregorianChange(new Date(Long.MIN_VALUE));
        Date dateToConvert = calendar.getTime();

        ConfirmationToken token = new ConfirmationToken();
        token.setConfirmationToken("ec9f508e-2063-4057-840f-efce2d1bbae5");
        token.setUser(getUser());
        token.setCreatedDate(dateToConvert);
        return token;
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
