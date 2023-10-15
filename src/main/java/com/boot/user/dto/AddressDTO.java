package com.boot.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    @Schema(description = "Unique identifier of the address.",
            example = "1")
    private long id;
    @Schema(description = "First name of the user.", example = "Constantine")
    @Size(min = 3, message = "Min First name size is 3 characters!")
    @Size(max = 30, message = "Max First name size is 30 characters!")
    @NotNull
    private String firstName;
    @Schema(description = "Last name of the user.", example = "Abigail")
    @Size(min = 3, message = "Min Last name size is 3 characters!")
    @Size(max = 30, message = "Max Last name size is 30 characters!")
    @NotNull
    private String lastName;
    @Schema(description = "Phone number of the user.", example = "0740000000")
    @Pattern(regexp="^(?=[07]{2})(?=\\d{10}).*", message = "Invalid Phone Number!")
    @NotNull
    private String phoneNumber;
    @Schema(description = "Email of the user.", example = "jellofirsthand@gmail.com")
    @Email(message = "Invalid Email!")
    @NotNull
    private String email;
    @Schema(description = "Country of the user.", example = "Romania")
    @Size(min = 4, message = "Min Country size is 4 characters!")
    @Size(max = 60, message = "Max Country size is 60 characters!")
    @NotNull
    private String country;
    @Schema(description = "County of the user.", example = "Cluj")
    @Size(min = 3, message = "Min County size is 3 characters!")
    @Size(max = 20, message = "Max County size is 30 characters!")
    @NotNull
    private String county;
    @Schema(description = "City of the user.", example = "Cluj-Napoca")
    @Size(min = 2, message = "Min City size is 2 characters!")
    @Size(max = 30, message = "Max City size is 30 characters!")
    @NotNull
    private String city;
    @Schema(description = "Postal code of the user.", example = "400335")
    @Pattern(regexp="\\d{6}",message="Invalid Postal code")
    @NotNull
    private String postalCode;
    @Schema(description = "Street of the user.", example = "Louis Pasteur 58")
    @Size(min = 3, message = "Min Street size is 3 characters!")
    @Size(max = 100, message = "Max Street size is 30 characters!")
    @NotNull
    private String street;
}
