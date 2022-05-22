package com.boot.user.service;


import com.boot.user.client.CartServiceClient;
import com.boot.user.dto.UserDTO;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.model.ConfirmationToken;
import com.boot.user.model.PasswordResetToken;
import com.boot.user.model.User;
import com.boot.user.repository.ConfirmationTokenRepository;
import com.boot.user.repository.PasswordResetTokenRepository;
import com.boot.user.repository.UserRepository;
import com.boot.user.validator.TokenValidator;
import com.boot.user.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.boot.user.model.User.*;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private TokenValidator tokenValidator;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailService emailSenderService;

    @Autowired
    private CartServiceClient cartServiceClient;

    @Autowired
    private PasswordResetTokenRepository passwordReserTokenRepository;

    @Transactional
    public UserDTO addUser(UserDTO userDTO) {
        log.info("addUser - process started");

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userDTO.setRole("USER");

        User user = userRepository.save(dtoToUserEntity(userDTO).setCreatedOn(LocalDate.now()));

        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);

        emailSenderService.sendConfirmationEmail(user, confirmationToken);

        return userEntityToDto(user);
    }

    public void confirmUserAccount(String confirmationToken)
            throws EntityNotFoundException, UnableToModifyDataException {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            if (!tokenValidator.checkTokenAvailability(token.getCreatedDate())) {
                throw new EntityNotFoundException("Token Expired!");
            }
            UserDTO user = getUserByEmail(token.getUser().getEmail());

            if (user.isActivated()) {
                throw new UnableToModifyDataException("User was already confirmed!");
            }
            user.setActivated(true);

            userRepository.save(dtoToUserEntity(user));
        } else {
            throw new EntityNotFoundException("Token not found!");
        }
    }

    public UserDTO updateUserByEmail(String email, UserDTO userDTO) {

        User user = userRepository.getUserByEmail(email);

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
        user.setLastUpdatedOn(LocalDate.now());

        userRepository.save(user);

        return userEntityToDto(user);
    }

    public UserDTO getUserById(long id) throws EntityNotFoundException {
        if (!userValidator.isIdPresent(id)) {
            throw new EntityNotFoundException("Id: " + id + " not found in the Database!");
        }
        User user = userRepository.getUserById(id);
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

    public UserDTO getUserByEmail(String email) throws EntityNotFoundException {
        log.info("getUserByEmail - process started");
        if (userValidator.isEmailPresent(email)) {
            throw new EntityNotFoundException("Email: " + email + " not found in the Database!");
        }

        User user = userRepository.getUserByEmail(email);
        return userEntityToDto(user);
    }

    public void deleteUserById(long id) throws EntityNotFoundException {
        if (!userValidator.isIdPresent(id)) {
            throw new EntityNotFoundException("Id: " + id + " not found in the Database!");
        }
        userRepository.deleteById(id);
    }

    public void deleteUserByEmail(String email) throws EntityNotFoundException {
        log.info("deleteUserByEmail - process started");
        if (userValidator.isEmailPresent(email)) {
            throw new EntityNotFoundException("Email: " + email + " not found in the Database!");
        }

        UserDTO userDto = getUserByEmail(email);

        userDto.getUserFavorites().clear();

        cartServiceClient.callDeleteCartByEmail(email);

        userRepository.deleteByEmail(email);
    }

    public void requestResetPassword(String userEmail) throws EntityNotFoundException {

        User user = userRepository.getUserByEmail(userEmail);

        if (user == null) {
            throw new EntityNotFoundException("Invalid Email adress!");
        }
        PasswordResetToken confirmationToken = new PasswordResetToken(user);

        passwordReserTokenRepository.save(confirmationToken);

        emailSenderService.sendPasswordResetEmail(user, confirmationToken);
    }

    public void changeUserPassword(String confirmationToken, String newPassword, String confirmedNewPassword)
            throws  EntityNotFoundException, UnableToModifyDataException {

        PasswordResetToken token = passwordReserTokenRepository.findByResetToken(confirmationToken);

        if (token != null) {
            if (!tokenValidator.checkTokenAvailability(token.getCreatedDate())) {
                throw new EntityNotFoundException("Token Expired!");
            }

            UserDTO userDto = getUserByEmail(token.getUser().getEmail());

            if (!userDto.isActivated()) {
                throw new UnableToModifyDataException("User was not activated!");
            }

            if (!newPassword.contentEquals(confirmedNewPassword)) {
                throw new EntityNotFoundException("Passwords do not mactch!");
            }

            if (passwordEncoder.matches(newPassword, userDto.getPassword())) {
                throw new EntityNotFoundException("Please select another password, this one was already used last time!");
            }

            userDto.setPassword(passwordEncoder.encode(newPassword));
            updateUserByEmail(userDto.getEmail(), userDto);

        } else {
            throw new EntityNotFoundException("Token not found!");
        }
    }

}
