package com.boot.user.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.boot.services.model.User;

import java.util.List;

@Slf4j
@Service   // It has to be annotated with @Service.
public class UserDetailsServiceImpl implements UserDetailsService  {
	

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
				
		User userEntity = restTemplate.getForObject("https://spring-store-user-service.herokuapp.com/getUserByEmail?email=" + email, User.class);
		
		if(userEntity != null) {

	        	if(!userEntity.isActivated()) {
	        		throw new UsernameNotFoundException("User using Email: " + email + " not activated!");
	        	}		
				// Remember that Spring needs roles to be in this format: "ROLE_" + userRole (i.e. "ROLE_ADMIN")
				// So, we need to set it to that format, so we can verify and compare roles (i.e. hasRole("ADMIN")).
				List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + userEntity.getRole());
				
				log.info("User using Email: " + userEntity.getEmail() + " logged in!");
		   	   // The "User" class is provided by Spring and represents a model class for user to be returned by UserDetailsService
		       // And used by auth manager to verify and check user authentication.				
			return new org.springframework.security.core.userdetails.User(userEntity.getEmail(),userEntity.getPassword(), grantedAuthorities);
		}

		// If user not found. Throw this exception.
		throw new UsernameNotFoundException("Email : " + email + " not found!");
	}

}