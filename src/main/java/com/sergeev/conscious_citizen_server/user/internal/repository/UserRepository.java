package com.sergeev.conscious_citizen_server.user.internal.repository;

import com.sergeev.conscious_citizen_server.user.api.dto.UsersForAdmin;
import com.sergeev.conscious_citizen_server.user.internal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByLogin(String login);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByLogin(String login);

    Optional<User> findById(Long id);

    @Query("""
    SELECT new com.sergeev.conscious_citizen_server.user.api.dto.UsersForAdmin(
        u.id,
        u.fullName,
        u.email,
        u.role.name,
        u.active,
        COUNT(i.id)
    )
    FROM User u
    LEFT JOIN Incident i ON i.userId = u.id AND i.active = true
    GROUP BY u.id, u.fullName, u.email, u.role.name, u.active
""")
    List<UsersForAdmin> findUsersWithIncidentCount();
}
