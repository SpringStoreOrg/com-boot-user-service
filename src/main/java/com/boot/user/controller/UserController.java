package com.boot.user.controller;

import java.util.List;
import java.util.Set;

import com.boot.user.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.boot.services.dto.ProductDTO;
import com.boot.services.dto.UserDTO;
import com.boot.user.exception.DuplicateEntryException;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @ResponseBody
    public ResponseEntity<UserDTO> addUser(@Valid @RequestBody UserDTO user) {
        UserDTO newUser = userService.addUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/{email}")
    public ResponseEntity<UserDTO> updateUserByEmail(@Valid @RequestBody UserDTO userDTO, @Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email) {
        UserDTO user = userService.updateUserByEmail(email, userDTO);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/confirm/{token}", method = {RequestMethod.GET, RequestMethod.PUT})
    public ResponseEntity<String> confirmUserAccount(@PathVariable("token") String token)
            throws EntityNotFoundException, UnableToModifyDataException {
        userService.confirmUserAccount(token);
        return new ResponseEntity<>("User activated Succesfully!", HttpStatus.OK);
    }

    @GetMapping("/id")
    @ResponseBody
    public ResponseEntity<UserDTO> findByUserId(@RequestParam long id) throws EntityNotFoundException {
        UserDTO user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getAllUsers() throws EntityNotFoundException {
        List<UserDTO> userList = userService.getAllUsers();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<UserDTO> getUserByEmail(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @RequestParam String email) throws EntityNotFoundException {
        UserDTO user = userService.getUserByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteUserById(@PathVariable("id") long id) throws EntityNotFoundException {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<UserDTO> deleteUserByByEmail(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
            throws EntityNotFoundException {
        userService.deleteUserByEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
