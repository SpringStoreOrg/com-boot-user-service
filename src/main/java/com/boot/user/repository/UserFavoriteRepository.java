package com.boot.user.repository;

import com.boot.user.model.User;
import com.boot.user.model.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

    void deleteByUserAndProductName(User user, String productName);
    UserFavorite findByUserAndProductName(User user, String productName);
    List<UserFavorite> findAllByUser(User user);
}
