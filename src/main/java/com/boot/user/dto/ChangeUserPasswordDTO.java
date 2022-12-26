package com.boot.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserPasswordDTO {

    @Size(min = 12, message = "Min Token size is 12 characters!")
    @Size(max = 50, message = "Max Token size is 20 characters!")
    String token;

    @Size(min = 8, message = "Min Password size is 8 characters!")
    @Size(max = 50, message = "Max Password size is 50 characters!")
    String newPassword;

    @Size(min = 8, message = "Min Password size is 8 characters!")
    @Size(max = 50, message = "Max Password size is 50 characters!")
    String confirmedNewPassword;
}
