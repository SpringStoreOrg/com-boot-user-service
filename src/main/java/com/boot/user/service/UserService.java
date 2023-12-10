package com.boot.user.service;


import com.boot.user.client.CartServiceClient;
import com.boot.user.dto.ChangeUserPasswordDTO;
import com.boot.user.dto.CreateUserDTO;
import com.boot.user.dto.CustomerMessageDTO;
import com.boot.user.dto.GetUserDTO;
import com.boot.user.exception.EmailAlreadyUsedException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.model.*;
import com.boot.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final EmailService emailSenderService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final RoleRepository roleRepository;

    private final CustomerMessageRepository customerMessageRepository;

    private final ProductRetrieverService productRetrieverService;

    private final CartServiceClient cartServiceClient;

    private final ModelMapper modelMapper;

    @Value("${activated.users.regex}")
    private String activatedUsersRegex;

    @Transactional
    public CreateUserDTO addUser(@NotNull CreateUserDTO userDTO) {
        log.info("addUser - process started");

        if(userRepository.existsByEmail(userDTO.getEmail())){
            throw new EmailAlreadyUsedException();
        }

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User inputUser = modelMapper.map(userDTO, User.class);

        inputUser.setRoleList(roleRepository.findAllInList(Arrays.asList("ACCESS", "CREATE_ORDER")));

        if (inputUser.getEmail().matches(activatedUsersRegex)) {
            inputUser.setVerified(true);
        }
        User user = userRepository.save(inputUser);

        if (!inputUser.isVerified()) {
            ConfirmationToken confirmationToken = new ConfirmationToken(user);

            confirmationTokenRepository.save(confirmationToken);

            emailSenderService.sendConfirmationEmail(user, confirmationToken);
        }

        GetUserDTO result = modelMapper.map(user, GetUserDTO.class);
        result.setRoles(getRolesForUser(user));

        return result;
    }

    @Transactional
    public void confirmUserAccount(String confirmationToken){
        log.info("confirmUserAccount - process started");
        ConfirmationToken token = confirmationTokenRepository.findByToken(confirmationToken);

        if (token != null) {
            if (checkTokenAvailability(token.getCreatedDate())) {
                throw new EntityNotFoundException("Token Expired!");
            }
            User user = userRepository.getUserByEmail(token.getUser().getEmail());

            if (user.isVerified()) {
                throw new UnableToModifyDataException("User was already confirmed!");
            }
            user.setVerified(true);

            userRepository.save(user);
        } else {
            throw new EntityNotFoundException("Token not found!");
        }
    }

    @Transactional
    public CreateUserDTO updateUserByEmail(String email, @NotNull CreateUserDTO userDTO){
        log.info("updateUserByEmail - process started");

        User user = userRepository.getUserByEmail(email);

        if (user == null) {
            throw new EntityNotFoundException("Invalid Email address!");
        }

        if(StringUtils.isNotBlank(userDTO.getEmail())) {
            user.setEmail(userDTO.getEmail());
        }
        if(StringUtils.isNotBlank(userDTO.getFirstName())) {
            user.setFirstName(userDTO.getFirstName());
        }

        if(StringUtils.isNotBlank(userDTO.getLastName())){
            user.setLastName(userDTO.getLastName());
        }

        if(StringUtils.isNotBlank(userDTO.getPhoneNumber())) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }

        if(StringUtils.isNotBlank(userDTO.getPassword())) {
            user.setPassword(userDTO.getPassword());
        }

        userRepository.save(user);

        GetUserDTO result = modelMapper.map(user, GetUserDTO.class);
        result.setRoles(getRolesForUser(user));

        return result;
    }

    public List<CreateUserDTO> getAllUsers() throws EntityNotFoundException {

        List<User> userList = userRepository.findAll();

        if (userList.isEmpty()) {
            throw new EntityNotFoundException("No user found in the Database!");
        }
        List<CreateUserDTO> userDTOList = new ArrayList<>();

        userList.forEach(u -> userDTOList.add(modelMapper.map(u, CreateUserDTO.class)));
        return userDTOList;
    }

    public GetUserDTO getUserByEmail(String email, boolean includeDetails, boolean includePassword) {
        log.info("getUserByEmail - process started");
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("Email: " + email + " not found in the Database!");
        }
        GetUserDTO result = modelMapper.map(user, GetUserDTO.class);
        result.setRoles(getRolesForUser(user));
        if (!includePassword) {
            result.setPassword(null);
        }
        if (includeDetails) {
            result.setUserFavorites(productRetrieverService.getProductDTOS(user));
        }

        return result;
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        log.info("deleteCartByUserId - process started");
        User user = userRepository.getUserByEmail(email);

        if (user == null) {
            throw new EntityNotFoundException("Email: " + email + " not found in the Database!");
        }

        ResponseEntity response = cartServiceClient.deleteCartByUserId(user.getId());


        if (response.getStatusCode().is2xxSuccessful()) {
            userRepository.deleteUserByEmail(email);
            log.info("deleteUserByEmail - successfully deleted");
        }
    }

    @Transactional
    public void requestResetPassword(String userEmail){

        User user = userRepository.getUserByEmail(userEmail);

        if (user == null) {
            throw new EntityNotFoundException("Invalid Email address!");
        }
        PasswordResetToken confirmationToken = new PasswordResetToken(user);

        passwordResetTokenRepository.save(confirmationToken);

        emailSenderService.sendPasswordResetEmail(user, confirmationToken);
    }

    @Transactional
    public void changeUserPassword(ChangeUserPasswordDTO changeUserPasswordDTO)
            throws  EntityNotFoundException, UnableToModifyDataException {
        if (!changeUserPasswordDTO.getNewPassword().contentEquals(changeUserPasswordDTO.getConfirmedNewPassword())) {
            throw new EntityNotFoundException("Passwords do not match!");
        }

        PasswordResetToken token = passwordResetTokenRepository.findByResetToken(changeUserPasswordDTO.getToken());

        if (token != null) {
            if (checkTokenAvailability(token.getCreatedDate())) {
                throw new EntityNotFoundException("Token Expired!");
            }

            GetUserDTO userDto = getUserByEmail(token.getUser().getEmail(), false, true);

            if (!userDto.isVerified()) {
                throw new UnableToModifyDataException("User was not activated!");
            }

            if (passwordEncoder.matches(changeUserPasswordDTO.getNewPassword(), userDto.getPassword())) {
                throw new EntityNotFoundException("Please select another password, this one was already used last time!");
            }

            userDto.setPassword(passwordEncoder.encode(changeUserPasswordDTO.getNewPassword()));
            updateUserByEmail(userDto.getEmail(), userDto);

        } else {
            throw new EntityNotFoundException("Token not found!");
        }
    }

    @Transactional
    public void saveCustomerMessage(@NotNull CustomerMessageDTO customerMessage) {
        log.info("customerMessage - process started");

        CustomerMessage message = modelMapper.map(customerMessage, CustomerMessage.class);

        customerMessageRepository.save(message);
    }

    private List<String> getRolesForUser(User user) {
        return (user.getRoleList() != null)
                ? (user.getRoleList().stream().map(Role::getName).collect(Collectors.toList()))
                : List.of();
    }

    private boolean checkTokenAvailability(@NotNull Date date) {
        return !LocalDateTime.now().isBefore(Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().plusDays(1));
    }
}
