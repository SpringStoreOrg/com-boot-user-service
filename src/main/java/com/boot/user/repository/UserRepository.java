package com.boot.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.boot.user.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User getUserById(long id);
	
	User getUserByEmail(String email);

	@Transactional
	void deleteByEmail(String email);

}
