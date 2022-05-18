package com.boot.user.controller;

import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.service.UserService;
import com.boot.user.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

@Controller
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/reset/{email}", method = {RequestMethod.GET, RequestMethod.PUT})
    public ResponseEntity<String> requestResetPassword(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
            throws EntityNotFoundException {
        userService.requestResetPassword(email);
        return new ResponseEntity<>("Request to reset Password succesfully send!", HttpStatus.OK);
    }

    @RequestMapping(value = "/change/{token}/{newPassword}/{confirmedNewPassword}", method = {
            RequestMethod.GET, RequestMethod.PUT})
    public ResponseEntity<String> changeUserPassword(@PathVariable("token") String token,
                                                     @PathVariable("newPassword") String newPassword,
                                                     @PathVariable("confirmedNewPassword") String confirmedNewPassword)
            throws EntityNotFoundException, UnableToModifyDataException {
        userService.changeUserPassword(token, newPassword, confirmedNewPassword);
        return new ResponseEntity<>("Password succesfully changed!", HttpStatus.OK);
    }

}
