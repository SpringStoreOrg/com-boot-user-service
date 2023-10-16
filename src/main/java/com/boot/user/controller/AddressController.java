package com.boot.user.controller;

import com.boot.user.dto.AddressDTO;
import com.boot.user.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Validated
@Controller
@AllArgsConstructor
@Tag(name = "address", description = "the Address API")
@Log4j2
@RequestMapping("/address")
public class AddressController {
    private AddressService addressService;
    @Operation(summary = "Save an address", description = "Save an address with the given information", tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address saved",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddressDTO> addAddress(@RequestHeader(value = "User-Id") long userId,
                                                 @Parameter(description = "Address to save. Cannot null or empty.",
                                                         required = true, schema = @Schema(implementation = AddressDTO.class))
                                           @Valid @RequestBody AddressDTO address) {
        AddressDTO saved = addressService.save(userId, address);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }
}
