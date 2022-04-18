package com.boot.user.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import com.boot.user.util.Constants;
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

import com.boot.services.dto.ProductDTO;
import com.boot.services.dto.UserDTO;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.InvalidInputDataException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.service.UserService;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

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

    @PutMapping("/addProductToUserFavorites/{email}/{productName}")
    public ResponseEntity<UserDTO> addProductToUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email,
                                                             @Size(min = 2, max = 30, message = "Product Name size has to be between 2 and 30 characters!") @PathVariable("productName") String productName)
            throws DuplicateEntryException {
        UserDTO user = userService.addProductToUserFavorites(email, productName);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/removeProductFromUserFavorites/{email}/{productName}")
    public ResponseEntity<UserDTO> removeProductFromUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email,
                                                                  @Size(min = 2, max = 30, message = "Product Name size has to be between 2 and 30 characters!") @PathVariable("productName") String productName)
            throws EntityNotFoundException {
        UserDTO user = userService.removeProductFromUserFavorites(email, productName);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/getAllProductsFromUserFavorites/{email}")
    @ResponseBody
    public ResponseEntity<Set<ProductDTO>> getAllProductsFromUserFavorites(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
            throws EntityNotFoundException {
        Set<ProductDTO> productList = userService.getAllProductsFromUserFavorites(email);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @PutMapping("/updateUserByEmail/{email}")
    public ResponseEntity<UserDTO> updateUserByEmail(@RequestBody UserDTO userDTO, @Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
            throws InvalidInputDataException {
        UserDTO user = userService.updateUserByEmail(email, userDTO);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/confirmUserAccount/{token}", method = {RequestMethod.GET, RequestMethod.PUT})
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

    @GetMapping("/getUserByEmail")
    @ResponseBody
    public ResponseEntity<UserDTO> getUserByEmail(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @RequestParam String email) throws EntityNotFoundException {
        UserDTO user = userService.getUserByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/deleteUserById/{id}")
    public ResponseEntity<UserDTO> deleteUserById(@PathVariable("id") long id) throws EntityNotFoundException {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteUserByEmail/{email}")
    public ResponseEntity<UserDTO> deleteUserByByEmail(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
            throws EntityNotFoundException {
        userService.deleteUserByEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/requestResetPassword/{email}", method = {RequestMethod.GET, RequestMethod.PUT})
    public ResponseEntity<String> requestResetPassword(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
            throws EntityNotFoundException {
        userService.requestResetPassword(email);
        return new ResponseEntity<>("Request to reset Password succesfully send!", HttpStatus.OK);
    }

    @RequestMapping(value = "/changeUserPassword/{token}/{newPassword}/{confirmedNewPassword}", method = {
            RequestMethod.GET, RequestMethod.PUT})
    public ResponseEntity<String> changeUserPassword(@PathVariable("token") String token,
                                                     @PathVariable("newPassword") String newPassword,
                                                     @PathVariable("confirmedNewPassword") String confirmedNewPassword)
            throws EntityNotFoundException, InvalidInputDataException, UnableToModifyDataException, ParseException {
        userService.changeUserPassword(token, newPassword, confirmedNewPassword);
        return new ResponseEntity<>("Password succesfully changed!", HttpStatus.OK);
    }

}
