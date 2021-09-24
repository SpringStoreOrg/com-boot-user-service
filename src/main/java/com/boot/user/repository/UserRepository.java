package com.boot.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.boot.services.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	public User getUserById(long id);
	
	public User getUserByEmail(String email);

	@Transactional
	public void deleteByEmail(String email);

}
