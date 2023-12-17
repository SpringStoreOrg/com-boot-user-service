package com.boot.user.controller;

import com.boot.user.dto.CreateUserDTO;
import com.boot.user.dto.CustomerMessageDTO;
import com.boot.user.dto.GetUserDTO;
import com.boot.user.service.UserService;
import com.boot.user.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@AllArgsConstructor
@Tag(name = "user", description = "the User API")
@Log4j2
public class UserController {

    private UserService userService;

    @Operation(summary = "Create a new user", description = "Create a new user with the given information", tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = CreateUserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)})
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateUserDTO> addUser(@Parameter(description = "User to add. Cannot null or empty.",
            required = true, schema = @Schema(implementation = CreateUserDTO.class))
                                           @Valid @RequestBody CreateUserDTO user) {
        CreateUserDTO userDTO = userService.addUser(user);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing user by email", description = "Update an existing user using user's email address", tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(schema = @Schema(implementation = CreateUserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @PutMapping(value = "/{email}", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<CreateUserDTO> updateUserByEmail(@Parameter(description = "User to be updated.",
            required = true, schema = @Schema(implementation = CreateUserDTO.class)) @Valid @RequestBody CreateUserDTO userDTO,
                                                           @Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email){
        CreateUserDTO user = userService.updateUserByEmail(email, userDTO);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Activate user account", description = "Activate user account using the confirmation token", tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User activated Successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Token not found", content = @Content)})
    @GetMapping(value = "/confirm/{token}")
    public ResponseEntity<String> confirmUserAccount(@PathVariable("token") String token){
        userService.confirmUserAccount(token);
        return new ResponseEntity<>("User activated Successfully!", HttpStatus.OK);
    }

    @Operation(summary = "Get all users", tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CreateUserDTO.class))))})
    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity<List<CreateUserDTO>> getAllUsers(){
        List<CreateUserDTO> userList = userService.getAllUsers();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @Operation(summary = "Get user by email", description = "Get a specific user by email address", tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully", content = @Content(schema = @Schema(implementation = CreateUserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @GetMapping("/{email}")
    @ResponseBody
    public ResponseEntity<GetUserDTO> getUserByEmail(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email,
                                                     @RequestParam(value = "includeDetails", required = false, defaultValue = "false") boolean includeDetails,
                                                     @RequestParam(value = "includePassword", required = false, defaultValue = "false") boolean includePassword){
        GetUserDTO user = userService.getUserByEmail(email, includeDetails, includePassword);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Delete user by email", description = "Delete a specific user by email address", tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully deleted", content = @Content()),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @DeleteMapping("/{email}")
    public ResponseEntity<CreateUserDTO> deleteUserByByEmail(@Email(message = "Invalid email!", regexp = Constants.EMAIL_REGEXP) @PathVariable("email") String email){
        userService.deleteUserByEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Save customer message", tags = {"customerMessage"})
    @PostMapping(value = "/customerMessage")
    public ResponseEntity<String> sendEmailFromUser(@Valid @RequestBody CustomerMessageDTO customerMessage) {
        userService.saveCustomerMessage(customerMessage);
        return new ResponseEntity<>("Customer Message saved!", HttpStatus.OK);
    }
}
