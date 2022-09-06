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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

        assertEquals(getUserDTO(), savedUser);

        assertNotNull(confirmationToken);
        assertEquals(getUser(), confirmationToken.getUser());
        assertNotNull(confirmationToken.getConfirmationToken());
        assertNotNull(confirmationToken.getCreatedDate());


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

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.confirmUserAccount(token().getConfirmationToken()));

        assertEquals("Token not found!", exception.getMessage());

       verifyNoInteractions(tokenValidator);
       verifyNoInteractions(userRepository);
    }

    @Test
    public void confirmUserAccount_tokenExpired() {

        when(confirmationTokenRepository.findByConfirmationToken(any())).thenReturn(token());
        when(tokenValidator.checkTokenAvailability(any())).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
            userService.confirmUserAccount(token().getConfirmationToken()));

        assertEquals("Token Expired!", exception.getMessage());

        verifyNoInteractions(userRepository);
    }

    @Test
    public void confirmUserAccount_userAlreadyConfirmed() {

        when(confirmationTokenRepository.findByConfirmationToken(any())).thenReturn(token());
        when(tokenValidator.checkTokenAvailability(any())).thenReturn(false);
        when(userRepository.getUserByEmail(token().getUser().getEmail())).thenReturn(getUser().setActivated(true));

        UnableToModifyDataException exception = assertThrows(UnableToModifyDataException.class, () ->
            userService.confirmUserAccount(token().getConfirmationToken()));

        assertEquals("User was already confirmed!", exception.getMessage());

        verify(userRepository).getUserByEmail(token().getUser().getEmail());
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    public void updateUserByEmail() throws EntityNotFoundException {

        when(userRepository.getUserByEmail(token().getUser().getEmail()))
                .thenReturn(getUser()
                        .setActivated(true));

        UserDTO updatedUser =  userService.updateUserByEmail(getUserDTO().getEmail(),getUserDTO()
                .setFirstName("newTestName")
                .setLastName("newTestLastName")
                .setPhoneNumber("0742999999")
                .setEmail("jon278@gaailer.com")
                .setRole("USER")
                .setDeliveryAddress("street, no. 12"));

        verify(userRepository).save(getUser()
                .setFirstName("newTestName")
                .setLastName("newTestLastName")
                .setPhoneNumber("0742999999")
                .setEmail("jon278@gaailer.com")
                .setRole("USER")
                .setDeliveryAddress("street, no. 12")
                .setActivated(true).setLastUpdatedOn(LocalDate.now()));

        assertEquals("newTestName", updatedUser.getFirstName());
        assertEquals("newTestLastName", updatedUser.getLastName());
        assertEquals("0742999999", updatedUser.getPhoneNumber());
        assertEquals("jon278@gaailer.com", updatedUser.getEmail());
        assertEquals("street, no. 12", updatedUser.getDeliveryAddress());
    }

    @Test
    public void updateUserByEmail_emptyUserDTO() throws EntityNotFoundException {

        when(userRepository.getUserByEmail(token().getUser().getEmail()))
                .thenReturn(getUser()
                        .setActivated(true));

        UserDTO updatedUser =  userService.updateUserByEmail(getUserDTO().getEmail(), new UserDTO());

        verify(userRepository).save(getUser()
                .setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setEmail("jon278@gaailer.site")
                .setDeliveryAddress("street, no. 1")
                .setActivated(true).setLastUpdatedOn(LocalDate.now()));

        assertEquals("testName", updatedUser.getFirstName());
        assertEquals("testLastName", updatedUser.getLastName());
        assertEquals("0742000000", updatedUser.getPhoneNumber());
        assertEquals("jon278@gaailer.site", updatedUser.getEmail());
        assertEquals("street, no. 1", updatedUser.getDeliveryAddress());
    }

    @Test
    public void updateUserByEmail_nullEmail() {

        when(userRepository.getUserByEmail(any())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.updateUserByEmail(getUserDTO().getEmail(), getUserDTO()));

        assertEquals("Invalid Email address!", exception.getMessage());

        verify(userRepository).getUserByEmail(token().getUser().getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void getAllUsers() throws EntityNotFoundException {

        List<User> userList = new ArrayList<>();
        userList.add(getUser());
        userList.add(getUser());

        when(userRepository.findAll()).thenReturn(userList);

        List<UserDTO> newUserList = userService.getAllUsers();

        assertEquals(newUserList.size(),2);
        verify(userRepository).findAll();

        userList.stream().forEach(user -> assertEquals(getUser(), user));
    }

    @Test
    public void getAllUsers_emptyUserList() {
        List<User> userList = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(userList);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.getAllUsers());

        assertEquals("No user found in the Database!", exception.getMessage());
    }

    @Test
    public void getUserByEmail() throws EntityNotFoundException {

        when(userRepository.getUserByEmail(getUser().getEmail())).thenReturn(getUser());
        when(userValidator.isEmailPresent(getUser().getEmail())).thenReturn(false);

        UserDTO userDTO = userService.getUserByEmail(getUser().getEmail());

        verify(userRepository).getUserByEmail(getUser().getEmail());
        verify(userValidator).isEmailPresent(getUser().getEmail());

        assertEquals(getUserDTO(), userDTO);
    }

    @Test
    public void getUserByEmail_emailNotPresent() {
        when(userValidator.isEmailPresent(getUser().getEmail())).thenReturn(true);
        when(userRepository.getUserByEmail(getUser().getEmail())).thenReturn(getUser());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.getUserByEmail(getUser().getEmail()));

        assertEquals("Email: " + getUser().getEmail() + " not found in the Database!", exception.getMessage());
        verifyNoInteractions(userRepository);

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
                .setDeliveryAddress("street, no. 1");

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
                .setDeliveryAddress("street, no. 1");

        return user;
    }

}
