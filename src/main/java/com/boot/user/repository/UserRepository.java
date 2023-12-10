package com.boot.user.repository;

import com.boot.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User getUserById(long id);
	
	User getUserByEmail(String email);

	@Modifying
	@Query("delete from User where email = :email")
	void deleteUserByEmail(@Param("email") String email);

	boolean existsByEmail(String email);
}
