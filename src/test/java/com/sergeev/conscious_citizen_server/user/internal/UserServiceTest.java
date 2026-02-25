package com.sergeev.conscious_citizen_server.user.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private UserService userService;

    private Long userId;
    private User activeUser;

    @BeforeEach
    void setUp() {
        SecureRandom secureRandom = new SecureRandom();
        userId = Math.abs(secureRandom.nextLong());

        activeUser = new User(
                userId,
                "John Doe",
                "Russia",
                "test@mail.com",
                "+12345678901",
                "encoded-password",
                null,
                null,
                true
        );
    }

    // =============================
    // LOGIN
    // =============================

    @Test
    void shouldLoginByEmailSuccessfully() {

        when(repository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches("raw-password", "encoded-password"))
                .thenReturn(true);

        Long result = userService.login("test@mail.com", "raw-password");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userId, result);
    }

    @Test
    void shouldLoginByPhoneSuccessfully() {

        when(repository.findByPhone("+12345678901"))
                .thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches("raw-password", "encoded-password"))
                .thenReturn(true);

        Long result = userService.login("+12345678901", "raw-password");

        Assertions.assertEquals(userId, result);
    }

    @Test
    void shouldThrowIfUserNotFound() {

        when(repository.findByEmail("unknown@mail.com"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.login("unknown@mail.com", "pass")
        );
    }

    @Test
    void shouldThrowIfPasswordInvalid() {

        when(repository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches("wrong", "encoded-password"))
                .thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.login("test@mail.com", "wrong")
        );
    }

    @Test
    void shouldThrowIfUserInactive() {

        User inactiveUser = new User(
                userId,
                "John Doe",
                "Russia",
                "test@mail.com",
                "+12345678901",
                "encoded-password",
                null,
                null,
                false
        );

        when(repository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(inactiveUser));

        when(passwordEncoder.matches("raw-password", "encoded-password"))
                .thenReturn(true);

        Assertions.assertThrows(IllegalStateException.class, () ->
                userService.login("test@mail.com", "raw-password")
        );
    }
}
