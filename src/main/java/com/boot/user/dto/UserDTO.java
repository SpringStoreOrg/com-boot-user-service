package com.boot.user.dto;

import com.boot.user.model.UserFavorite;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.*;
import java.util.List;

@Data
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	@Schema(description = "Unique identifier of the user.",
			example = "1")
	private long id;

	@Schema(description = "First name of the user.",
			example = "Constantine")
	@Size(min = 3, message = "Min First name size is 3 characters!")
	@Size(max = 30, message = "Max First name size is 30 characters!")
	private String firstName;

	@Schema(description = "Last name of the user.",
			example = "Abigail")
	@Size(min = 3, message = "Min Last name size is 3 characters!")
	@Size(max = 30, message = "Max Last name size is 30 characters!")
	private String lastName;

	@Schema(description = "Password of the user.",
			example = "testPassword1234@@")
	@Size(min = 8, message = "Min Password size is 8 characters!")
	@Size(max = 30, message = "Max Password size is 30 characters!")
	private String password;

	@Schema(description = "Phone number of the user.",
			example = "0740000000")

	@Pattern(regexp="^(?=[07]{2})(?=\\d{10}).*", message = "Invalid Phone Number!")
	private String phoneNumber;

	@Schema(description = "Email of the user.",
			example = "jellofirsthand@gmail.com")
	@Email(message = "Invalid Email!")
	private String email;

	@Schema(description = "Address line of the contact.",
			example = "888 Constantine Ave, #54")
	@Size(min = 3, message = "Min Delivery address size is 8 characters!")
	@Size(max = 300, message = "Max Delivery address size is 300 characters!")
	private String deliveryAddress;

	@Schema(description = "User role",
			example = "USER")
	private List<String> roles;

	private List<UserFavorite> userFavorites;
	
	private boolean isActivated;

}