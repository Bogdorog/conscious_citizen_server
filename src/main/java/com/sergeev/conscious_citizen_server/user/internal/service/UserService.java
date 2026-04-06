package com.sergeev.conscious_citizen_server.user.internal.service;

import com.sergeev.conscious_citizen_server.user.api.dto.AuthResult;
import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;
import com.sergeev.conscious_citizen_server.user.api.dto.UsersForAdmin;
import com.sergeev.conscious_citizen_server.user.api.dto.request.*;
import com.sergeev.conscious_citizen_server.user.api.event.PasswordResetRequestedEvent;
import com.sergeev.conscious_citizen_server.user.api.event.UserLoggedInEvent;
import com.sergeev.conscious_citizen_server.user.api.event.UserProfileUpdatedEvent;
import com.sergeev.conscious_citizen_server.user.api.event.UserRegisteredEvent;
import com.sergeev.conscious_citizen_server.user.internal.entity.PasswordResetToken;
import com.sergeev.conscious_citizen_server.user.internal.entity.Role;
import com.sergeev.conscious_citizen_server.user.internal.entity.User;
import com.sergeev.conscious_citizen_server.user.internal.mapper.UserMapper;
import com.sergeev.conscious_citizen_server.user.internal.repository.PasswordResetTokenRepository;
import com.sergeev.conscious_citizen_server.user.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/*
Список функций:
регистрация 1
вход 1
изменение профиля 1
данные профиля переделать
смена пароля 1
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository repository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordChangeService passwordChangeService;

    @Transactional
    public Long register(RegisterUserRequest request) {

        if (repository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Пользователь с таким e-mail уже существует");
        }

        String hash = passwordEncoder.encode(request.password());
        // У всех по умолчанию роль пользователя
        Role role = new Role(2L, "USER");

        User user = repository.save(
                User.builder()
                        .login(request.login())
                        .fullName(request.fullName())
                        .email(request.email())
                        .phone(request.phone())
                        .address(request.address())
                        .passwordHash(hash)
                        .role(role)
                        .active(true)
                        .build()
        );

        publisher.publishEvent(new UserRegisteredEvent(user.getId()));

        return user.getId();
    }

    public AuthResult login(LoginRequest request) {

        User user = repository.findByLogin(request.login())
                            .orElseThrow(() ->
                                    new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("User inactive");
        }

        publisher.publishEvent(new UserLoggedInEvent(user.getId()));

        return new AuthResult(user.getId().toString());
    }

    @Transactional
    public UserDto updateProfile(UpdateProfileRequest request) {

        User user = repository.findByLogin(request.login())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        user.setAddress(request.address());

        publisher.publishEvent(new UserProfileUpdatedEvent(user.getId()));

        return userMapper.toResponse(user);
    }

    public UserDto get(String login) {
        User user = repository.findByLogin(login)
                .orElseThrow();
        return userMapper.toResponse(user);
    }

    public UserDto getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow();
        return userMapper.toResponse(user);
    }

    public String getRole(String login) {
        User user = repository.findByLogin(login)
                .orElseThrow();
        return user.getRole().getName();
    }

    public List<UsersForAdmin> getUsersForAdmin() {
        return repository.findUsersWithIncidentCount();
    }

    // Первый запрос о смене пароля
    @Transactional
    public void initiatePasswordReset(PasswordResetRequest request) {

        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String rawToken = passwordChangeService.generateToken();
        String tokenHash = passwordChangeService.hashToken(rawToken);

        PasswordResetToken resetToken =
                new PasswordResetToken(
                        user.getId(),
                        tokenHash,
                        LocalDateTime.now().plusHours(1)
                );

        tokenRepository.save(resetToken);

        // публикуем событие
        publisher.publishEvent(
                new PasswordResetRequestedEvent(
                        user.getEmail(),
                        rawToken
                )
        );
    }
    // Успешный ввод токена, смена пароля
    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {

        String tokenHash = passwordChangeService.hashToken(request.token());

        PasswordResetToken resetToken =
                tokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new IllegalStateException("Token expired");
        }

        if (resetToken.isUsed()) {
            throw new IllegalStateException("Token already used");
        }

        User user = repository.findById(resetToken.getUserId())
                .orElseThrow();

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        tokenRepository.invalidateAllForUser(user.getId());

        resetToken.markUsed();

        repository.save(user);
        tokenRepository.save(resetToken);
    }
}