package com.markmihalkovich.course.repository;

import com.markmihalkovich.course.entity.User;
import com.markmihalkovich.course.entity.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(Long id);

    Optional<List<User>> findUsersByRoles(ERole role);
}
