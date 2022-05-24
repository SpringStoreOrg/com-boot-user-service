package com.boot.user.model;


import com.boot.user.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "user")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class User implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2904101271253876784L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String firstName;

	@Column
	private String lastName;

	@Column
	private String password;

	@Column
	private String phoneNumber;

	@Size(min = 3, max = 50)
	@Column(unique = true)
	private String email;

	@Column
	private String deliveryAddress;

	@Column
	private LocalDate createdOn;

	@Column
	private LocalDate lastUpdatedOn;
	
	@Column
	private String role;

	@JsonManagedReference
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY,  cascade = { CascadeType.ALL })
	private List<UserFavorite> userFavorites;

	@Column
	private boolean isActivated;

	public static UserDTO userEntityToDto(@NotNull User user) {
		return new UserDTO()
				.setId(user.getId())
				.setFirstName(user.getFirstName())
				.setLastName(user.getLastName())
				.setPassword(user.getPassword())
				.setPhoneNumber(user.getPhoneNumber())
				.setEmail(user.getEmail())
				.setDeliveryAddress(user.getDeliveryAddress())
				.setRole(user.getRole())
				.setUserFavorites(user.getUserFavorites())
				.setActivated(user.isActivated());

	}

	public static User dtoToUserEntity(@NotNull UserDTO userDto) {
		return new User()
				.setId(userDto.getId())
				.setFirstName(userDto.getFirstName())
				.setLastName(userDto.getLastName())
				.setPassword(userDto.getPassword())
				.setPhoneNumber(userDto.getPhoneNumber())
				.setDeliveryAddress(userDto.getDeliveryAddress())
				.setEmail(userDto.getEmail())
				.setRole(userDto.getRole())
				.setUserFavorites(userDto.getUserFavorites())
				.setActivated(userDto.isActivated());
	}

}