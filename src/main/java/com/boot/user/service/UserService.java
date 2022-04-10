package com.boot.user.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.boot.services.dto.ProductDTO;
import com.boot.services.dto.UserDTO;
import com.boot.services.mapper.UserMapper;
import com.boot.services.model.User;
import com.boot.user.client.CartServiceClient;
import com.boot.user.client.ProductServiceClient;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.InvalidInputDataException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.model.ConfirmationToken;
import com.boot.user.model.PasswordResetToken;
import com.boot.user.repository.ConfirmationTokenRepository;
import com.boot.user.repository.PasswordResetTokenRepository;
import com.boot.user.repository.UserRepository;
import com.boot.user.validator.TokenValidator;
import com.boot.user.validator.UserValidator;

import lombok.extern.slf4j.Slf4j;

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
	private ProductServiceClient productServiceClient;
	
	@Autowired
	private CartServiceClient cartServiceClient;

	@Autowired
	private PasswordResetTokenRepository passwordReserTokenRepository;

	public UserDTO addProductToUserFavorites(String email, String productName) throws DuplicateEntryException {

		ProductDTO productDTO = productServiceClient.callGetProductByProductName(productName).getBody();

		User user = userRepository.getUserByEmail(email);

		UserDTO userDTOMapped = UserMapper.UserEntityToDto(user);

		if (userDTOMapped.getFavoriteProductList().stream().anyMatch(p -> productName.equals(p.getProductName()))) {
			throw new DuplicateEntryException("Product: " + productName + " was already added to favorites!");
		} else {
			userDTOMapped.getFavoriteProductList().add(productDTO);
			userRepository.save(UserMapper.updateDtoToUserEntity(user, userDTOMapped));

			return UserMapper.UserEntityToDto(user);
		}
	}

	public UserDTO removeProductFromUserFavorites(String email, String productName) throws EntityNotFoundException {

		ProductDTO productDTO = productServiceClient.callGetProductByProductName(productName).getBody();

		User user = userRepository.getUserByEmail(email);

		UserDTO userDTOMapped = UserMapper.UserEntityToDto(user);

		if (userDTOMapped.getFavoriteProductList().stream().anyMatch(p -> productName.equals(p.getProductName()))) {

			userDTOMapped.getFavoriteProductList().remove(productDTO);
			user = userRepository.save(UserMapper.updateDtoToUserEntity(user, userDTOMapped));

			return UserMapper.UserEntityToDto(user);
		} else {
			throw new EntityNotFoundException("Product: " + productName + " was not found in the favorites list!");
		}
	}
	
	public Set<ProductDTO> getAllProductsFromUserFavorites(String email) throws EntityNotFoundException {

		User user = userRepository.getUserByEmail(email);

		UserDTO userDTOMapped = UserMapper.UserEntityToDto(user);

		return userDTOMapped.getFavoriteProductList();
	}

	//TODO add @Transactional on this method as you do multiple save operations. You don't want a persisted new user without confirmation token.
	public UserDTO addUser(UserDTO userDTO) throws InvalidInputDataException {
		log.info("addUser - process started");
		if (!userValidator.isEmailValid(userDTO.getEmail())) {
			throw new InvalidInputDataException("invalid email format!");
		}

		if (!userValidator.isUserDataSizeCorrect(userDTO.getEmail(), 3, 30)) {
			throw new InvalidInputDataException("Provided Email has to be between 3 and 30 characters long!");
		}

		if (!userValidator.isUserDataSizeCorrect(userDTO.getFirstName(), 3, 30)) {
			throw new InvalidInputDataException("Name has to be between 3 and 30 characters long!");
		}
		if (!userValidator.isUserDataSizeCorrect(userDTO.getLastName(), 3, 30)) {
			throw new InvalidInputDataException("Surname has to be between 3 and 30 characters long!");
		}

		if (!userValidator.isUserDataSizeCorrect(userDTO.getDeliveryAddress(), 3, 300)) {
			throw new InvalidInputDataException("Address has to be between 3 and 300 characters long!");
		}
		if (!userValidator.isPhoneNumberValid(userDTO.getPhoneNumber())) {
			throw new InvalidInputDataException("invalid phone number format!");
		}
		if (!userValidator.isUserDataSizeCorrect(userDTO.getPhoneNumber(), 10, 15)) {
			throw new InvalidInputDataException("Phone number has to be between 10 and 15 characters long!");
		}

		userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		
		userDTO.setRole("USER");
		
		User user = userRepository.save(UserMapper.DtoToUserEntity(userDTO).setCreatedOn(LocalDate.now()));

		ConfirmationToken confirmationToken = new ConfirmationToken(user);

		confirmationTokenRepository.save(confirmationToken);

		emailSenderService.sendConfirmationEmail(user, confirmationToken);

		return UserMapper.UserEntityToDto(user);
	}

	public void confirmUserAccount(String confirmationToken)
			throws InvalidInputDataException, EntityNotFoundException, UnableToModifyDataException, ParseException {
		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

		if (token != null) {
			if (tokenValidator.checkTokenAvailability(token.getCreatedDate())) {
				throw new EntityNotFoundException("Token Expired!");
			}

			UserDTO user = getUserByEmail(token.getUser().getEmail());

			if (user.isActivated() == true) {
				throw new UnableToModifyDataException("User was already confirmed!");
			}
			user.setActivated(true);
			//if you call this you do all the validation on userDTO, and this does not make sense as you load the data from DB
			updateUserByEmail(user.getEmail(), user);

		} else {
			throw new EntityNotFoundException("Token not found!");
		}
	}

	public UserDTO updateUserByEmail(String email, UserDTO userDTO) throws InvalidInputDataException {

		User user = userRepository.getUserByEmail(email);

		UserDTO userDTOMapped = UserMapper.UserEntityToDto(user);

		if (!userValidator.isEmailValid(userDTO.getEmail())) {
			throw new InvalidInputDataException("invalid email format!");
		}

		if (!userValidator.isUserDataSizeCorrect(userDTO.getEmail(), 3, 30)) {
			throw new InvalidInputDataException("Provided Email has to be between 3 and 30 characters long!");
		}
		userDTOMapped.setEmail(userDTO.getEmail());

		if (!userValidator.isUserDataSizeCorrect(userDTO.getFirstName(), 3, 30)) {
			throw new InvalidInputDataException("Name has to be between 3 and 30 characters long!");
		}
		userDTOMapped.setFirstName(userDTO.getFirstName());

		if (!userValidator.isUserDataSizeCorrect(userDTO.getLastName(), 3, 30)) {
			throw new InvalidInputDataException("Surname has to be between 3 and 30 characters long!");
		}

		userDTOMapped.setLastName(userDTO.getLastName());

		if (!userValidator.isUserDataSizeCorrect(userDTO.getDeliveryAddress(), 3, 300)) {
			throw new InvalidInputDataException("Adress has to be between 3 and 300 characters long!");
		}

		userDTOMapped.setDeliveryAddress(userDTO.getDeliveryAddress());

		if (!userValidator.isPhoneNumberValid(userDTO.getPhoneNumber())) {
			throw new InvalidInputDataException("invalid phone number format!");
		}
		if (!userValidator.isUserDataSizeCorrect(userDTO.getPhoneNumber(), 10, 15)) {
			throw new InvalidInputDataException("Phone number has to be between 10 and 15 characters long!");
		}
		userDTOMapped.setPhoneNumber(userDTO.getPhoneNumber());

		userDTOMapped.setPassword(userDTO.getPassword());

		userDTOMapped.setFavoriteProductList(userDTO.getFavoriteProductList());

		userDTOMapped.setActivated(userDTO.isActivated());

		user = userRepository
				.save(UserMapper.updateDtoToUserEntity(user, userDTOMapped).setLastUpdatedOn(LocalDate.now()));

		return userDTOMapped;
	}

	public UserDTO getUserById(long id) throws EntityNotFoundException {
		if (!userValidator.isIdPresent(id)) {
			throw new EntityNotFoundException("Id: " + id + " not found in the Database!");
		}
		User user = userRepository.getUserById(id);
		return UserMapper.UserEntityToDto(user);
	}

	public List<UserDTO> getAllUsers() throws EntityNotFoundException {
		//try to call findAll only once
		if (userRepository.findAll() == null || userRepository.findAll().isEmpty()) {
			throw new EntityNotFoundException("No user found in the Database!");
		}
		List<User> userList = userRepository.findAll();
		List<UserDTO> userDTOList = new ArrayList<>();

		userList.stream().forEach(u -> userDTOList.add(UserMapper.UserEntityToDto(u)));
		return userDTOList;
	}

	public UserDTO getUserByEmail(String email) throws EntityNotFoundException {
		log.info("getUserByEmail - process started");
		if (userValidator.isEmailPresent(email)) {
			throw new EntityNotFoundException("Email: " + email + " not found in the Database!");
		}

		User user = userRepository.getUserByEmail(email);
		return UserMapper.UserEntityToDto(user);
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

		userDto.getFavoriteProductList().clear();

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
			throws InvalidInputDataException, EntityNotFoundException, UnableToModifyDataException, ParseException {
		
		PasswordResetToken token = passwordReserTokenRepository.findByResetToken(confirmationToken);

		if (token != null) {			
			if (tokenValidator.checkTokenAvailability(token.getCreatedDate())) {
				throw new EntityNotFoundException("Token Expired!");
			}

			UserDTO userDto = getUserByEmail(token.getUser().getEmail());

			if (userDto.isActivated() != true) {
				throw new UnableToModifyDataException("User was not activated!");
			}
			
			if (!newPassword.contentEquals(confirmedNewPassword)) {
				throw new EntityNotFoundException("Passwords do not mactch!");
			}
						
			if (passwordEncoder.matches(newPassword, userDto.getPassword())) {
				throw new EntityNotFoundException("Please select another password, this one was already used last time!");
			}

			//don't you need to encode the password here?
			userDto.setPassword(newPassword);
			updateUserByEmail(userDto.getEmail(), userDto);

		} else {
			throw new EntityNotFoundException("Token not found!");
		}
	}

}
