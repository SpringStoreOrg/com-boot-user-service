package com.boot.user.dto;

import com.boot.user.model.UserFavorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.util.List;

@Data
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private long id;

	@Size(min = 3, message = "Min First name size is 3 characters!")
	@Size(max = 30, message = "Max First name size is 30 characters!")
	private String firstName;

	@Size(min = 3, message = "Min Last name size is 3 characters!")
	@Size(max = 30, message = "Max Last name size is 30 characters!")
	private String lastName;

	@Size(min = 8, message = "Min Password size is 8 characters!")
	@Size(max = 30, message = "Max Password size is 30 characters!")
	private String password;

	@Pattern(regexp="^(?=[07]{2})(?=\\d{10}).*", message = "Invalid Phone Number!")
	private String phoneNumber;

	@Email(regexp="^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Invalid Email!")
	private String email;


	@Size(min = 3, message = "Min Delivery address size is 8 characters!")
	@Size(max = 300, message = "Max Delivery address size is 300 characters!")
	private String deliveryAddress;

	private String role;

	private List<UserFavorite> userFavorites;
	
	private boolean isActivated;

}