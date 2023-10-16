package com.boot.user.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
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
	private LocalDateTime createdOn;

	@Column
	private LocalDateTime lastUpdatedOn;
	
	@ManyToMany
	@JoinTable(name = "user_role",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roleList;

	@JsonManagedReference
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY,  cascade = { CascadeType.ALL })
  	private transient List<UserFavorite> userFavorites;

	@Column
	private boolean verified;

	@OneToOne(mappedBy = "user")
	private Address address;

	@PrePersist
	public void create(){
		this.createdOn = LocalDateTime.now();
		this.lastUpdatedOn = LocalDateTime.now();
	}

	@PreUpdate
	public void update(){
		this.lastUpdatedOn = LocalDateTime.now();
	}

}