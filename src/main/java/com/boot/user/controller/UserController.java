package com.boot.user.controller;

import com.boot.user.dto.UserDTO;
import com.boot.user.exception.EntityNotFoundException;
import com.boot.user.exception.UnableToModifyDataException;
import com.boot.user.service.UserService;
import com.boot.user.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;

@Validated
@Controller
@Tag(name = "user", description = "the User API")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Create a new user", description = "Create a new user with the given information", tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)})
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> addUser(@Parameter(description = "User to add. Cannot null or empty.",
            required = true, schema = @Schema(implementation = UserDTO.class))
                                           @Valid @RequestBody UserDTO user) {
       UserDTO userDTO =  userService.addUser(user);
        return new ResponseEntity<>(userDTO,HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing user by email", description = "Update an existing user using user's email address", tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated" , content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @PutMapping(value = "/{email}", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<UserDTO> updateUserByEmail(@Parameter(description = "User to be updated.",
            required = true, schema = @Schema(implementation = UserDTO.class)) @Valid @RequestBody UserDTO userDTO,
                                                     @Parameter(description = "Email of the user to be updated. Cannot be empty.",
                                                             required = true) @Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email) throws EntityNotFoundException {
        UserDTO user = userService.updateUserByEmail(email, userDTO);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Activate user account", description = "", tags = {"user"})
    @PutMapping(value = "/confirm/{token}")
    public ResponseEntity<String> confirmUserAccount(@PathVariable("token") String token)
            throws EntityNotFoundException, UnableToModifyDataException {
        userService.confirmUserAccount(token);
        return new ResponseEntity<>("User activated Successfully!", HttpStatus.OK);
    }

    @Operation(summary = "Get all users", description = "", tags = {"user"})
    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getAllUsers() throws EntityNotFoundException {
        List<UserDTO> userList = userService.getAllUsers();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @Operation(summary = "Get user by email", description = "", tags = {"user"})
    @GetMapping
    @ResponseBody
    public ResponseEntity<UserDTO> getUserByEmail(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @RequestParam String email) throws EntityNotFoundException {
        UserDTO user = userService.getUserByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Delete user by email", description = "", tags = {"user"})
    @DeleteMapping("/{email}")
    public ResponseEntity<UserDTO> deleteUserByByEmail(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email)
            throws EntityNotFoundException {
        userService.deleteUserByEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
