package com.sergeev.conscious_citizen_server.user.internal.repository;

import com.sergeev.conscious_citizen_server.user.internal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByPhone(String phone);
}
