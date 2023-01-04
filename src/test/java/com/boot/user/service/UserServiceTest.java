package com.boot.user.service;


import com.boot.user.dto.ChangeUserPasswordDTO;
import com.boot.user.dto.UserDTO;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.model.ConfirmationToken;
import com.boot.user.model.PasswordResetToken;
import com.boot.user.model.Role;
import com.boot.user.model.User;
import com.boot.user.repository.ConfirmationTokenRepository;
import com.boot.user.repository.PasswordResetTokenRepository;
import com.boot.user.repository.RoleRepository;
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

import java.util.*;

import static com.boot.user.model.User.userEntityToDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

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

    @Captor
    private ArgumentCaptor<PasswordResetToken> passwordResetTokenCaptor;

    @Test
    void testAddUser() {

        when(passwordEncoder.encode(getUserDTO().getPassword())).thenReturn("testPassword");
        when(userRepository.save(getUser())).thenReturn(getUser());
        when(confirmationTokenRepository.save(any())).thenReturn(new ConfirmationToken());
        when(roleRepository.findAllInList(eq(Arrays.asList("ACCESS", "CREATE_ORDER")))).thenReturn(Arrays.asList(getRole("ACCESS"), getRole("CREATE_ORDER")));

        UserDTO savedUser = userService.addUser(getUserDTO());

        verify(roleRepository).findAllInList(eq(Arrays.asList("ACCESS", "CREATE_ORDER")));
        verify(userRepository).save(getUser());

        verify(confirmationTokenRepository).save(confirmationTokenCaptor.capture());

        ConfirmationToken confirmationToken = confirmationTokenCaptor.getValue();

        verify(confirmationTokenRepository).save(confirmationToken);
        verify(emailSenderService).sendConfirmationEmail(getUser(), confirmationToken);

        assertEquals(getUserDTO(), savedUser);

        assertNotNull(confirmationToken);
        assertEquals(getUser(), confirmationToken.getUser());
        assertNotNull(confirmationToken.getToken());
        assertNotNull(confirmationToken.getCreatedDate());


    }

    @Test
    void testConfirmUserAccount() throws EntityNotFoundException, UnableToModifyDataException {

        ConfirmationToken token = getToken();

        when(confirmationTokenRepository.findByToken(any())).thenReturn(token);
        when(userRepository.getUserByEmail(token.getUser().getEmail())).thenReturn(getUser());

        userService.confirmUserAccount(token.getToken());

        verify(userRepository).getUserByEmail(getToken().getUser().getEmail());
        verify(tokenValidator).checkTokenAvailability(token.getCreatedDate());
        verify(userRepository).save(getUser().setActivated(true));
    }

    @Test
    void testConfirmUserAccount_nullToken() {

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.confirmUserAccount(getToken().getToken()));

        assertEquals("Token not found!", exception.getMessage());

        verifyNoInteractions(tokenValidator);
        verifyNoInteractions(userRepository);
    }

    @Test
    void testConfirmUserAccount_tokenExpired() {

        when(confirmationTokenRepository.findByToken(any())).thenReturn(getToken());
        when(tokenValidator.checkTokenAvailability(any())).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.confirmUserAccount(getToken().getToken()));

        assertEquals("Token Expired!", exception.getMessage());

        verifyNoInteractions(userRepository);
    }

    @Test
    void testConfirmUserAccount_userAlreadyConfirmed() {

        when(confirmationTokenRepository.findByToken(any())).thenReturn(getToken());
        when(tokenValidator.checkTokenAvailability(any())).thenReturn(false);
        when(userRepository.getUserByEmail(getToken().getUser().getEmail())).thenReturn(getUser().setActivated(true));

        UnableToModifyDataException exception = assertThrows(UnableToModifyDataException.class, () ->
                userService.confirmUserAccount(getToken().getToken()));

        assertEquals("User was already confirmed!", exception.getMessage());

        verify(userRepository).getUserByEmail(getToken().getUser().getEmail());
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    void testUpdateUserByEmail() throws EntityNotFoundException {

        when(userRepository.getUserByEmail(getToken().getUser().getEmail()))
                .thenReturn(getUser()
                        .setActivated(true));

        UserDTO updatedUser = userService.updateUserByEmail(getUserDTO().getEmail(), getUserDTO()
                .setFirstName("newTestName")
                .setLastName("newTestLastName")
                .setPhoneNumber("0742999999")
                .setEmail("jon278@gaailer.com")
                .setRoles(Arrays.asList("ACCESS", "CREATE_ORDER"))
                .setDeliveryAddress("street, no. 12"));

        verify(userRepository).save(getUser()
                .setFirstName("newTestName")
                .setLastName("newTestLastName")
                .setPhoneNumber("0742999999")
                .setEmail("jon278@gaailer.com")
                .setRoleList(Arrays.asList(getRole("ACCESS"), getRole("CREATE_ORDER")))
                .setDeliveryAddress("street, no. 12")
                .setActivated(true));

        assertEquals("newTestName", updatedUser.getFirstName());
        assertEquals("newTestLastName", updatedUser.getLastName());
        assertEquals("0742999999", updatedUser.getPhoneNumber());
        assertEquals("jon278@gaailer.com", updatedUser.getEmail());
        assertEquals("street, no. 12", updatedUser.getDeliveryAddress());
    }

    @Test
    void testUpdateUserByEmail_emptyUserDTO() throws EntityNotFoundException {

        when(userRepository.getUserByEmail(getToken().getUser().getEmail()))
                .thenReturn(getUser()
                        .setActivated(true));

        UserDTO updatedUser = userService.updateUserByEmail(getUserDTO().getEmail(), new UserDTO());

        verify(userRepository).save(getUser()
                .setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setEmail("jon278@gaailer.site")
                .setDeliveryAddress("street, no. 1")
                .setActivated(true));

        assertEquals("testName", updatedUser.getFirstName());
        assertEquals("testLastName", updatedUser.getLastName());
        assertEquals("0742000000", updatedUser.getPhoneNumber());
        assertEquals("jon278@gaailer.site", updatedUser.getEmail());
        assertEquals("street, no. 1", updatedUser.getDeliveryAddress());
    }

    @Test
    void testUpdateUserByEmail_nullEmail() {

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.updateUserByEmail(getUserDTO().getEmail(), getUserDTO()));

        assertEquals("Invalid Email address!", exception.getMessage());

        verify(userRepository).getUserByEmail(getToken().getUser().getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testGetAllUsers() throws EntityNotFoundException {

        List<User> userList = new ArrayList<>();
        userList.add(getUser());
        userList.add(getUser());

        when(userRepository.findAll()).thenReturn(userList);

        List<UserDTO> newUserList = userService.getAllUsers();

        assertEquals(2, newUserList.size());
        verify(userRepository).findAll();

        newUserList.forEach(user -> assertEquals(userEntityToDto(getUser()), user));
    }

    @Test
    void testGetAllUsers_emptyUserList() {
        List<User> userList = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(userList);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.getAllUsers());

        assertEquals("No user found in the Database!", exception.getMessage());
    }

    @Test
    void testGetUserByEmail() throws EntityNotFoundException {
        User user = getUser();
        user.setRoleList(Arrays.asList(getRole("ACCESS"), getRole("CREATE_ORDER")));

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(getUser());
        when(userValidator.isEmailPresent(user.getEmail())).thenReturn(true);

        UserDTO userDTO = userService.getUserByEmail(user.getEmail());

        verify(userRepository).getUserByEmail(user.getEmail());
        verify(userValidator).isEmailPresent(user.getEmail());

        assertEquals(getUserDTO(), userDTO);
    }

    @Test
    void testGetUserByEmail_emailNotPresent() {
        when(userValidator.isEmailPresent(getUser().getEmail())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.getUserByEmail(getUser().getEmail()));

        assertEquals("Email: " + getUser().getEmail() + " not found in the Database!", exception.getMessage());
        verifyNoInteractions(userRepository);

    }

    @Test
    void testDeleteUserByEmail() throws EntityNotFoundException {
        User user = getUser();

        when(userValidator.isEmailPresent(user.getEmail())).thenReturn(true);

        userService.deleteUserByEmail(user.getEmail());

        verify(userRepository).deleteByEmail(user.getEmail());
    }

    @Test
    void testDeleteUserByEmail_emailNotPresent() {
        when(userValidator.isEmailPresent(getUser().getEmail())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.deleteUserByEmail(getUser().getEmail()));

        assertEquals("Email: " + getUser().getEmail() + " not found in the Database!", exception.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testRequestResetPassword() throws EntityNotFoundException {
        User user = getUser();

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

        userService.requestResetPassword(user.getEmail());

        verify(passwordReserTokenRepository).save(passwordResetTokenCaptor.capture());

        PasswordResetToken passwordResetToken = passwordResetTokenCaptor.getValue();

        verify(passwordReserTokenRepository).save(passwordResetToken);

        verify(emailSenderService).sendPasswordResetEmail(user, passwordResetToken);

        assertNotNull(passwordResetToken);
        assertEquals(getUser(), passwordResetToken.getUser());
        assertNotNull(passwordResetToken.getResetToken());
        assertNotNull(passwordResetToken.getCreatedDate());

    }

    @Test
    void testRequestResetPassword_userNotFound() {

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.requestResetPassword(getUser().getEmail()));

        assertEquals("Invalid Email address!", exception.getMessage());

        verifyNoInteractions(passwordReserTokenRepository);
        verifyNoInteractions(emailSenderService);
    }

    @Test
    void testChangeUserPassword() throws EntityNotFoundException, UnableToModifyDataException {

        PasswordResetToken passwordResetToken = getPasswordResetToken();
        User user = getUser();

        String newPassword = "testNewPassword";

        when(passwordReserTokenRepository.findByResetToken(any())).thenReturn(passwordResetToken);
        when(tokenValidator.checkTokenAvailability(passwordResetToken.getCreatedDate())).thenReturn(false);
        when(userValidator.isEmailPresent(user.getEmail())).thenReturn(true);
        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user.setActivated(true));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        userService.changeUserPassword(changeUserPassword(passwordResetToken.getResetToken(),newPassword,newPassword));

        verify(passwordReserTokenRepository).findByResetToken(passwordResetToken.getResetToken());
        verify(tokenValidator).checkTokenAvailability(passwordResetToken.getCreatedDate());
        verify(passwordEncoder).matches(newPassword, user.getPassword());
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository, times(2)).getUserByEmail(user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testChangeUserPassword_tokenNotFound() {

        String newPassword = "testNewPassword";
        String testConfirmationToken = "testConfirmationToken";

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.changeUserPassword(changeUserPassword(testConfirmationToken,newPassword,newPassword)));

        assertEquals("Token not found!", exception.getMessage());

        verifyNoInteractions(tokenValidator);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userRepository);
    }

    @Test
    void testChangeUserPassword_tokenExpired() {

        String newPassword = "testNewPassword";
        String testConfirmationToken = "testConfirmationToken";
        PasswordResetToken passwordResetToken = getPasswordResetToken();

        when(passwordReserTokenRepository.findByResetToken(any())).thenReturn(passwordResetToken);
        when(tokenValidator.checkTokenAvailability(passwordResetToken.getCreatedDate())).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.changeUserPassword(changeUserPassword(testConfirmationToken,newPassword,newPassword)));

        assertEquals("Token Expired!", exception.getMessage());

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userValidator);
        verifyNoInteractions(userRepository);
    }

    @Test
    void testChangeUserPassword_userNotActivated() {
        User user = getUser();

        String newPassword = "testNewPassword";
        String testConfirmationToken = "testConfirmationToken";
        PasswordResetToken passwordResetToken = getPasswordResetToken();

        when(passwordReserTokenRepository.findByResetToken(any())).thenReturn(passwordResetToken);
        when(tokenValidator.checkTokenAvailability(passwordResetToken.getCreatedDate())).thenReturn(false);
        when(userValidator.isEmailPresent(user.getEmail())).thenReturn(true);
        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

        UnableToModifyDataException exception = assertThrows(UnableToModifyDataException.class, () ->
                userService.changeUserPassword(changeUserPassword(testConfirmationToken,newPassword,newPassword)));

        assertEquals("User was not activated!", exception.getMessage());

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void testChangeUserPassword_passwordsNotMatch() {
        User user = getUser();

        String newPassword = "testNewPassword";
        String invaldNewPassword = "testNewPassword123";
        String testConfirmationToken = "testConfirmationToken";
        PasswordResetToken passwordResetToken = getPasswordResetToken();

        when(passwordReserTokenRepository.findByResetToken(any())).thenReturn(passwordResetToken);
        when(tokenValidator.checkTokenAvailability(passwordResetToken.getCreatedDate())).thenReturn(false);
        when(userValidator.isEmailPresent(user.getEmail())).thenReturn(true);
        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user.setActivated(true));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.changeUserPassword(changeUserPassword(testConfirmationToken,newPassword,invaldNewPassword)));

        assertEquals("Passwords do not match!", exception.getMessage());

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void testChangeUserPassword_passwordAlreadyUsed() {
        User user = getUser();

        String invalidNewPassword = "testPassword";

        String testConfirmationToken = "testConfirmationToken";
        PasswordResetToken passwordResetToken = getPasswordResetToken();

        when(passwordReserTokenRepository.findByResetToken(any())).thenReturn(passwordResetToken);
        when(tokenValidator.checkTokenAvailability(passwordResetToken.getCreatedDate())).thenReturn(false);
        when(userValidator.isEmailPresent(user.getEmail())).thenReturn(true);
        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user.setActivated(true));
        when(passwordEncoder.matches(invalidNewPassword, getUser().getPassword())).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.changeUserPassword(changeUserPassword(testConfirmationToken,invalidNewPassword,invalidNewPassword)));

        assertEquals("Please select another password, this one was already used last time!", exception.getMessage());
    }

    private PasswordResetToken getPasswordResetToken() {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setGregorianChange(new Date(Long.MIN_VALUE));
        Date dateToConvert = calendar.getTime();

        return new PasswordResetToken()
                .setResetToken("ec9f508e-2063-4057-840f-efce2d1bbae5")
                .setUser(getUser())
                .setCreatedDate(dateToConvert);
    }

    private ConfirmationToken getToken() {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setGregorianChange(new Date(Long.MIN_VALUE));
        Date dateToConvert = calendar.getTime();

        ConfirmationToken token = new ConfirmationToken();
        token.setTokenId(1);
        token.setToken("ec9f508e-2063-4057-840f-efce2d1bbae5");
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
                .setRoles(Arrays.asList("ACCESS", "CREATE_ORDER"))
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
                .setRoleList(Arrays.asList(getRole("ACCESS"), getRole("CREATE_ORDER")))
                .setDeliveryAddress("street, no. 1");

        return user;
    }

    private ChangeUserPasswordDTO changeUserPassword(String token, String newPassword, String confirmedNewPassword){
        ChangeUserPasswordDTO changeUserPasswordDTO = new ChangeUserPasswordDTO();
        changeUserPasswordDTO.setToken(token);
        changeUserPasswordDTO.setNewPassword(newPassword);
        changeUserPasswordDTO.setConfirmedNewPassword(confirmedNewPassword);

        return changeUserPasswordDTO;
    }

    private Role getRole(String name){
        Role role = new Role();
        role.setName(name);

        return role;
    }
}
