package com.boot.user.service;


import com.boot.user.dto.AddressDTO;
import com.boot.user.dto.ChangeUserPasswordDTO;
import com.boot.user.dto.CreateUserDTO;
import com.boot.user.dto.GetUserDTO;
import com.boot.user.exception.EmailAlreadyUsedException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.model.*;
import com.boot.user.repository.ConfirmationTokenRepository;
import com.boot.user.repository.PasswordResetTokenRepository;
import com.boot.user.repository.RoleRepository;
import com.boot.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private EmailService emailSenderService;

    @Mock
    private PasswordResetTokenRepository passwordReserTokenRepository;

    @Captor
    private ArgumentCaptor<ConfirmationToken> confirmationTokenCaptor;

    @Captor
    private ArgumentCaptor<PasswordResetToken> passwordResetTokenCaptor;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userService, "activatedUsersRegex", ".*@springstore-test\\.com");
        ReflectionTestUtils.setField(userService, "modelMapper", getModelMapper());
    }

    @Test
    void testAddUser() {
        when(userRepository.existsByEmail(getUserDTO().getEmail())).thenReturn(false);
        when(passwordEncoder.encode(getUserDTO().getPassword())).thenReturn("testPassword");
        when(userRepository.save(getUser())).thenReturn(getUser());
        when(confirmationTokenRepository.save(any())).thenReturn(new ConfirmationToken());
        when(roleRepository.findAllInList(eq(Arrays.asList("ACCESS", "CREATE_ORDER")))).thenReturn(Arrays.asList(getRole("ACCESS"), getRole("CREATE_ORDER")));

        CreateUserDTO savedUser = userService.addUser(getUserDTO());

        verify(userRepository).existsByEmail(getUserDTO().getEmail());
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
    void testAddUserActivated() {

        String testEmail = "test1234@springstore-test.com";
        CreateUserDTO userDTO = getUserDTO();
        userDTO.setEmail(testEmail);

        User user = getUser();
        user.setEmail(testEmail);
        user.setVerified(true);

        when(userRepository.existsByEmail(testEmail)).thenReturn(false);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("testPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(roleRepository.findAllInList(eq(Arrays.asList("ACCESS", "CREATE_ORDER")))).thenReturn(Arrays.asList(getRole("ACCESS"), getRole("CREATE_ORDER")));

        CreateUserDTO savedUser = userService.addUser(userDTO);

        verify(userRepository).existsByEmail(testEmail);
        verify(passwordEncoder).encode(userDTO.getPassword());
        verify(roleRepository).findAllInList(eq(Arrays.asList("ACCESS", "CREATE_ORDER")));
        verify(userRepository).save(user);

        verifyNoInteractions(confirmationTokenRepository, emailSenderService);

        userDTO.setVerified(true);
        assertEquals(userDTO, savedUser);
    }

    @Test
    void testAddUserAlreadyExists() {
        when(userRepository.existsByEmail(getUserDTO().getEmail())).thenReturn(true);
        try{
            userService.addUser(getUserDTO());
            fail("Exception should have been thrown");
        }catch (EmailAlreadyUsedException e){
            verify(userRepository).existsByEmail(getUserDTO().getEmail());
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(confirmationTokenRepository, emailSenderService);
        }
    }

    @Test
    void testConfirmUserAccount() throws EntityNotFoundException, UnableToModifyDataException {

        ConfirmationToken token = getToken();

        when(confirmationTokenRepository.findByToken(any())).thenReturn(token);
        when(userRepository.getUserByEmail(token.getUser().getEmail())).thenReturn(getUser());

        userService.confirmUserAccount(token.getToken());

        verify(userRepository).getUserByEmail(getToken().getUser().getEmail());
        verify(userRepository).save(getUser().setVerified(true));
    }

    @Test
    void testConfirmUserAccount_nullToken() {

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.confirmUserAccount(getToken().getToken()));

        assertEquals("Token not found!", exception.getMessage());

        verifyNoInteractions(userRepository);
    }

    @Test
    void testConfirmUserAccount_tokenExpired() {
        ConfirmationToken token = getToken();
        token.setCreatedDate(Date.from(LocalDate.now().minusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC)));
        when(confirmationTokenRepository.findByToken(any())).thenReturn(token);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.confirmUserAccount(token.getToken()));

        assertEquals("Token Expired!", exception.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testConfirmUserAccount_userAlreadyConfirmed() {

        when(confirmationTokenRepository.findByToken(any())).thenReturn(getToken());
        when(userRepository.getUserByEmail(getToken().getUser().getEmail())).thenReturn(getUser().setVerified(true));

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
                        .setVerified(true));

        CreateUserDTO updatedUser = userService.updateUserByEmail(getUserDTO().getEmail(), getUserDTO()
                .setFirstName("newTestName")
                .setLastName("newTestLastName")
                .setPhoneNumber("0742999999")
                .setEmail("jon278@gaailer.com"));
        //updatedUser.setRoles(Arrays.asList("ACCESS", "CREATE_ORDER"));

        verify(userRepository).save(getUser()
                .setFirstName("newTestName")
                .setLastName("newTestLastName")
                .setPhoneNumber("0742999999")
                .setEmail("jon278@gaailer.com")
                .setRoleList(Arrays.asList(getRole("ACCESS"), getRole("CREATE_ORDER")))
                .setVerified(true));

        assertEquals("newTestName", updatedUser.getFirstName());
        assertEquals("newTestLastName", updatedUser.getLastName());
        assertEquals("0742999999", updatedUser.getPhoneNumber());
        assertEquals("jon278@gaailer.com", updatedUser.getEmail());
    }

    @Test
    void testUpdateUserByEmail_emptyUserDTO() throws EntityNotFoundException {

        when(userRepository.getUserByEmail(getToken().getUser().getEmail()))
                .thenReturn(getUser().setVerified(true));

        CreateUserDTO updatedUser = userService.updateUserByEmail(getUserDTO().getEmail(), new CreateUserDTO());

        verify(userRepository).save(getUser()
                .setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setEmail("jon278@gaailer.site")
                .setVerified(true));

        assertEquals("testName", updatedUser.getFirstName());
        assertEquals("testLastName", updatedUser.getLastName());
        assertEquals("0742000000", updatedUser.getPhoneNumber());
        assertEquals("jon278@gaailer.site", updatedUser.getEmail());
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

        List<CreateUserDTO> newUserList = userService.getAllUsers();

        assertEquals(2, newUserList.size());
        verify(userRepository).findAll();
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

        CreateUserDTO userDTO = userService.getUserByEmail(user.getEmail(), true, false);

        verify(userRepository).getUserByEmail(user.getEmail());

        assertEquals(getUserDTO(), userDTO);
    }

    @Test
    void testGetUserByEmail_emailNotPresent() {
        when(userRepository.getUserByEmail(getUser().getEmail())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.getUserByEmail(getUser().getEmail(), true, false));

        assertEquals("Email: " + getUser().getEmail() + " not found in the Database!", exception.getMessage());
    }

    @Test
    void testDeleteUserByEmail() throws EntityNotFoundException {
        User user = getUser();

        String email = user.getEmail();

        when(userRepository.getUserByEmail(email)).thenReturn(user);

        userService.deleteUserByEmail(email);

        verify(userRepository).deleteByEmail(email);
    }

    @Test
    void testDeleteUserByEmail_emailNotPresent() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.deleteUserByEmail(getUser().getEmail()));

        assertEquals("Email: " + getUser().getEmail() + " not found in the Database!", exception.getMessage());
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
        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user.setVerified(true));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        userService.changeUserPassword(changeUserPassword(passwordResetToken.getResetToken(),newPassword,newPassword));

        verify(passwordReserTokenRepository).findByResetToken(passwordResetToken.getResetToken());
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

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userRepository);
    }

    @Test
    void testChangeUserPassword_tokenExpired() {

        String newPassword = "testNewPassword";
        String testConfirmationToken = "testConfirmationToken";
        PasswordResetToken passwordResetToken = getPasswordResetToken();
        passwordResetToken.setCreatedDate(Date.from(LocalDate.now().minusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC)));

        when(passwordReserTokenRepository.findByResetToken(any())).thenReturn(passwordResetToken);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.changeUserPassword(changeUserPassword(testConfirmationToken,newPassword,newPassword)));

        assertEquals("Token Expired!", exception.getMessage());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void testChangeUserPassword_userNotActivated() {
        User user = getUser();

        String newPassword = "testNewPassword";
        String testConfirmationToken = "testConfirmationToken";
        PasswordResetToken passwordResetToken = getPasswordResetToken();

        when(passwordReserTokenRepository.findByResetToken(any())).thenReturn(passwordResetToken);
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
        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user.setVerified(true));

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
        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user.setVerified(true));
        when(passwordEncoder.matches(invalidNewPassword, getUser().getPassword())).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.changeUserPassword(changeUserPassword(testConfirmationToken,invalidNewPassword,invalidNewPassword)));

        assertEquals("Please select another password, this one was already used last time!", exception.getMessage());
    }

    private PasswordResetToken getPasswordResetToken() {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setGregorianChange(new Date());
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

    private GetUserDTO getUserDTO() {
        GetUserDTO userDTO = new GetUserDTO();

        userDTO.setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setPassword("testPassword")
                .setEmail("jon278@gaailer.site");
        userDTO.setRoles(Arrays.asList("ACCESS", "CREATE_ORDER"));

        return userDTO;
    }

    private User getUser() {
        User user = new User();

        user.setFirstName("testName")
                .setLastName("testLastName")
                .setPhoneNumber("0742000000")
                .setPassword("testPassword")
                .setEmail("jon278@gaailer.site")
                .setRoleList(Arrays.asList(getRole("ACCESS"), getRole("CREATE_ORDER")));

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

    private ModelMapper getModelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(User.class, CreateUserDTO.class);
        modelMapper.typeMap(User.class, GetUserDTO.class);
        modelMapper.typeMap(Address.class, AddressDTO.class);
        modelMapper.addMappings(new PropertyMap<Address, Address>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getCreatedOn());
                skip(destination.getLastUpdatedOn());
                skip(destination.getUser());
            }
        });

        return modelMapper;
    }
}
