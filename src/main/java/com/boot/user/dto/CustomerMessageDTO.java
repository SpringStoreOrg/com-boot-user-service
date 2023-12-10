package com.boot.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMessageDTO {

    @Size(min = 3, message = "Min Last name size is 3 characters!")
    @Size(max = 50, message = "Max Last name size is 50 characters!")
    private String name;

    @Email(message = "Invalid Email!")
    @Size(min = 3, message = "Min email size is 3 characters!")
    @Size(max = 100, message = "Max email size is 100 characters!")
    private String email;

    @Pattern(regexp="^(?=[07]{2})(?=\\d{10}).*", message = "Invalid Phone Number!")
    private String phoneNumber;

    @Size(min = 2, message = "Min comment size is 2 characters!")
    @Size(max = 550, message = "Max comment size is 550 characters!")
    private String comment;

}
