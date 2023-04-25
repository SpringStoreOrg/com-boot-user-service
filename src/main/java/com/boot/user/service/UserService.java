package com.boot.user.service;


import com.boot.user.dto.ChangeUserPasswordDTO;
import com.boot.user.dto.UserDTO;
import com.boot.user.exception.EmailAlreadyUsedException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.model.ConfirmationToken;
import com.boot.user.model.PasswordResetToken;
import com.boot.user.model.User;
import com.boot.user.repository.ConfirmationTokenRepository;
import com.boot.user.repository.PasswordResetTokenRepository;
import com.boot.user.repository.RoleRepository;
import com.boot.user.repository.UserRepository;
import com.boot.user.validator.TokenValidator;
import com.boot.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.boot.user.model.User.dtoToUserEntity;
import static com.boot.user.model.User.userEntityToDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserValidator userValidator;

    private final TokenValidator tokenValidator;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final EmailService emailSenderService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final RoleRepository roleRepository;

    @Value("${activated.users.regex}")
    private String activatedUsersRegex;

    @Transactional
    public UserDTO addUser(@NotNull UserDTO userDTO) {
        log.info("addUser - process started");

        if(userRepository.existsByEmail(userDTO.getEmail())){
            throw new EmailAlreadyUsedException();
        }

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User inputUser = dtoToUserEntity(userDTO);

        inputUser.setRoleList(roleRepository.findAllInList(Arrays.asList("ACCESS", "CREATE_ORDER")));

        if (inputUser.getEmail().matches(activatedUsersRegex)) {
            inputUser.setActivated(true);
        }
        User user = userRepository.save(inputUser);

        if (!inputUser.isActivated()) {
            ConfirmationToken confirmationToken = new ConfirmationToken(user);

            confirmationTokenRepository.save(confirmationToken);

            emailSenderService.sendConfirmationEmail(user, confirmationToken);
        }

        return userEntityToDto(user);
    }

    @Transactional
    public void confirmUserAccount(String confirmationToken){
        ConfirmationToken token = confirmationTokenRepository.findByToken(confirmationToken);

        if (token != null) {
            if (tokenValidator.checkTokenAvailability(token.getCreatedDate())) {
                throw new EntityNotFoundException("Token Expired!");
            }
            User user = userRepository.getUserByEmail(token.getUser().getEmail());

            if (user.isActivated()) {
                throw new UnableToModifyDataException("User was already confirmed!");
            }
            user.setActivated(true);

            userRepository.save(user);
        } else {
            throw new EntityNotFoundException("Token not found!");
        }
    }

    @Transactional
    public UserDTO updateUserByEmail(String email, @NotNull UserDTO userDTO){
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

        if(StringUtils.isNotBlank(userDTO.getDeliveryAddress())) {
            user.setDeliveryAddress(userDTO.getDeliveryAddress());
        }

        if(StringUtils.isNotBlank(userDTO.getPhoneNumber())) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }

        userRepository.save(user);

        return userEntityToDto(user);
    }

    public List<UserDTO> getAllUsers() throws EntityNotFoundException {

        List<User> userList = userRepository.findAll();

        if (userList.isEmpty()) {
            throw new EntityNotFoundException("No user found in the Database!");
        }
        List<UserDTO> userDTOList = new ArrayList<>();

        userList.forEach(u -> userDTOList.add(userEntityToDto(u)));
        return userDTOList;
    }

    public UserDTO getUserByEmail(String email){
        log.info("getUserByEmail - process started");
        if (!userValidator.isEmailPresent(email)) {
            throw new EntityNotFoundException("Email: " + email + " not found in the Database!");
        }

        User user = userRepository.getUserByEmail(email);
        return userEntityToDto(user);
    }

    @Transactional
    public void deleteUserByEmail(String email){
        log.info("deleteUserByEmail - process started");
        if (!userValidator.isEmailPresent(email)) {
            throw new EntityNotFoundException("Email: " + email + " not found in the Database!");
        }

        userRepository.deleteByEmail(email);
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

        PasswordResetToken token = passwordResetTokenRepository.findByResetToken(changeUserPasswordDTO.getToken());

        if (token != null) {
            if (tokenValidator.checkTokenAvailability(token.getCreatedDate())) {
                throw new EntityNotFoundException("Token Expired!");
            }

            UserDTO userDto = getUserByEmail(token.getUser().getEmail());

            if (!userDto.isActivated()) {
                throw new UnableToModifyDataException("User was not activated!");
            }

            if (!changeUserPasswordDTO.getNewPassword().contentEquals(changeUserPasswordDTO.getConfirmedNewPassword())) {
                throw new EntityNotFoundException("Passwords do not match!");
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

}
