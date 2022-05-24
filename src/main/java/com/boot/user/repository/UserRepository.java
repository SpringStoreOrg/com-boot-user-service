package com.boot.user.repository;

import com.boot.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User getUserById(long id);
	
	User getUserByEmail(String email);

	void deleteByEmail(String email);
}
