package com.boot.user.repository;

import com.boot.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("select r from Role r where r.name in (:names)")
    List<Role> findAllInList(@Param("names") List<String> names);
}
