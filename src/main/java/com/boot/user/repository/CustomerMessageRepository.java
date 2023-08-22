package com.boot.user.repository;

import com.boot.user.model.CustomerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerMessageRepository extends JpaRepository<CustomerMessage, Long> {

}
