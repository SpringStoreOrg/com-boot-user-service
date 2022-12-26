package com.boot.user.controller;

import com.boot.user.dto.ChangeUserPasswordDTO;
import com.boot.user.dto.UserDTO;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.service.UserService;
import com.boot.user.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@Validated
@Controller
@AllArgsConstructor
@Tag(name = "password", description = "the Password API")
@RequestMapping("/password")
public class PasswordController {

    private UserService userService;

    @Operation(summary = "Request reset password", tags = {"password"})
    @PutMapping(value = "/reset/{email}")
    public ResponseEntity<String> requestResetPassword(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
            throws EntityNotFoundException {
        userService.requestResetPassword(email);
        return new ResponseEntity<>("Request to reset Password successfully send!", HttpStatus.OK);
    }

    @Operation(summary = "Change user password", tags = {"password"})
    @PutMapping(value = "/change")
    public ResponseEntity<String> changeUserPassword(@Parameter(description = "New user Password to change. Cannot null or empty.",
            required = true, schema = @Schema(implementation = ChangeUserPasswordDTO.class))
                                                         @Valid @RequestBody ChangeUserPasswordDTO changeUserPasswordDTO)
            throws EntityNotFoundException, UnableToModifyDataException {
        userService.changeUserPassword(changeUserPasswordDTO);
        return new ResponseEntity<>("Password successfully changed!", HttpStatus.OK);
    }

}
