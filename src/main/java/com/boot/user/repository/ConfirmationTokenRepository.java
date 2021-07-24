package com.boot.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boot.user.model.ConfirmationToken;


@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

	 ConfirmationToken findByConfirmationToken(String confirmationToken);
}
