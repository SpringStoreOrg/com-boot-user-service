package com.boot.user.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boot.services.dto.UserDTO;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.InvalidInputDataException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.service.UserService;

@Controller
@RequestMapping("/")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/addUser")
	public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO user) throws InvalidInputDataException {
		UserDTO newUser = userService.addUser(user);
		return new ResponseEntity<>(newUser, HttpStatus.CREATED);
	}

	@PutMapping("/addProductToUserFavorites/{userName}/{productName}")
	public ResponseEntity<UserDTO> addProductToUserFavorites(@PathVariable("userName") String userName,
			@PathVariable("productName") String productName)
			throws EntityNotFoundException, InvalidInputDataException, DuplicateEntryException {
		UserDTO user = userService.addProductToUserFavorites(userName, productName);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PutMapping("/removeProductFromUserFavorites/{userName}/{productName}")
	public ResponseEntity<UserDTO> removeProductFromUserFavorites(@PathVariable("userName") String userName,
			@PathVariable("productName") String productName)
			throws EntityNotFoundException, InvalidInputDataException, DuplicateEntryException {
		UserDTO user = userService.removeProductFromUserFavorites(userName, productName);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PutMapping("/updateUserByUserName/{userName}")
	public ResponseEntity<UserDTO> updateUserByUsername(@RequestBody UserDTO userDTO,
			@PathVariable("userName") String userName) throws EntityNotFoundException, InvalidInputDataException {
		UserDTO user = userService.updateUserByUserName(userName, userDTO);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/confirmUserAccount/{token}", method = { RequestMethod.GET, RequestMethod.PUT })
	public ResponseEntity<String> confirmUserAccount(@PathVariable("token") String token)
			throws EntityNotFoundException, InvalidInputDataException, UnableToModifyDataException, ParseException {
		userService.confirmUserAccount(token);
		return new ResponseEntity<>("User activated Succesfully!", HttpStatus.OK);
	}

	@GetMapping("/getUserById")
	@ResponseBody
	public ResponseEntity<UserDTO> findByUserId(@RequestParam long id) throws EntityNotFoundException {
		UserDTO user = userService.getUserById(id);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@GetMapping("/getAllUsers")
	@ResponseBody
	public ResponseEntity<List<UserDTO>> getAllUsers() throws EntityNotFoundException {
		List<UserDTO> userList = userService.getAllUsers();
		return new ResponseEntity<>(userList, HttpStatus.OK);
	}

	@GetMapping("/getUserByUserName")
	@ResponseBody
	public ResponseEntity<UserDTO> getUserByUsername(@RequestParam String userName) throws EntityNotFoundException {
		UserDTO user = userService.getUserByUserName(userName);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@DeleteMapping("/deleteUserById/{id}")
	public ResponseEntity<UserDTO> deleteUserById(@PathVariable("id") long id) throws EntityNotFoundException {
		userService.deleteUserById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/deleteUserByUserName/{userName}")
	public ResponseEntity<UserDTO> deleteUserByUserName(@PathVariable("userName") String userName)
			throws EntityNotFoundException {
		userService.deleteUserByUserName(userName);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/requestResetPassword/{email}", method = { RequestMethod.GET, RequestMethod.PUT })
	public ResponseEntity<String> requestResetPassword(@PathVariable("email") String email)
			throws EntityNotFoundException {
		userService.requestResetPassword(email);
		return new ResponseEntity<>("Request to reset Password succesfully send!", HttpStatus.OK);
	}

	@RequestMapping(value = "/changeUserPassword/{token}/{newPassword}/{confirmedNewPassword}", method = {
			RequestMethod.GET, RequestMethod.PUT })
	public ResponseEntity<String> changeUserPassword(@PathVariable("token") String token,
			@PathVariable("newPassword") String newPassword,
			@PathVariable("confirmedNewPassword") String confirmedNewPassword)
			throws EntityNotFoundException, InvalidInputDataException, UnableToModifyDataException, ParseException {
		userService.changeUserPassword(token, newPassword, confirmedNewPassword);
		return new ResponseEntity<>("Password succesfully changed!", HttpStatus.OK);
	}

}
