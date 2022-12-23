package com.boot.user.controller;

import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.service.UserService;
import com.boot.user.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.Email;

@Validated
@Controller
@AllArgsConstructor
@RequestMapping("/password")
public class PasswordController {

    private UserService userService;

    @PutMapping(value = "/reset/{email}")
    public ResponseEntity<String> requestResetPassword(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
            throws EntityNotFoundException {
        userService.requestResetPassword(email);
        return new ResponseEntity<>("Request to reset Password successfully send!", HttpStatus.OK);
    }

    @PutMapping(value = "/change/{token}/{newPassword}/{confirmedNewPassword}")
    public ResponseEntity<String> changeUserPassword(@PathVariable("token") String token,
                                                     @PathVariable("newPassword") String newPassword,
                                                     @PathVariable("confirmedNewPassword") String confirmedNewPassword)
            throws EntityNotFoundException, UnableToModifyDataException {
        userService.changeUserPassword(token, newPassword, confirmedNewPassword);
        return new ResponseEntity<>("Password successfully changed!", HttpStatus.OK);
    }

}
