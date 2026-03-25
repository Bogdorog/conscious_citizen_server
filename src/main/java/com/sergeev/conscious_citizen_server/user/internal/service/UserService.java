package com.sergeev.conscious_citizen_server.user.internal.service;

import com.sergeev.conscious_citizen_server.user.api.dto.AuthResult;
import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;
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

        ContactIdentifier identifier =
                ContactIdentifierParser.parse(request.emailOrPhone());

        User user = switch (identifier) {

            case EmailIdentifier email ->
                    repository.findByEmail(email.value())
                            .orElseThrow(() ->
                                    new IllegalArgumentException("User not found"));

            case PhoneIdentifier phone ->
                    repository.findByPhone(phone.value())
                            .orElseThrow(() ->
                                    new IllegalArgumentException("User not found"));
        };

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("User inactive");
        }

        publisher.publishEvent(new UserLoggedInEvent(user.getId()));

        AuthResult auth = new AuthResult(user.getId().toString());
        return auth;
    }

    @Transactional
    public UserDto updateProfile(UpdateProfileRequest request) {

        User user = repository.findByEmail(request.email())
                .orElseThrow();

        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        user.setAddress(request.address());

        publisher.publishEvent(new UserProfileUpdatedEvent(user.getId()));

        return userMapper.toResponse(user);
    }

    public UserDto get(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow();
        return userMapper.toResponse(user);
    }
    // Первый запрос о смене пароля
    @Transactional
    public void initiatePasswordReset(PasswordResetRequest request) {

        User user = repository.findByEmail(request.emailOrPhone())
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

// Разделение почты и телефона происходит здесь
    sealed interface ContactIdentifier
            permits EmailIdentifier, PhoneIdentifier {

        String value();
    }

    record EmailIdentifier(String value)
            implements ContactIdentifier {

        public EmailIdentifier {
            if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Invalid email");
            }
        }
    }

    record PhoneIdentifier(String value)
            implements ContactIdentifier {

        public PhoneIdentifier {
            if (!value.matches("^\\+?[0-9]{10,15}$")) {
                throw new IllegalArgumentException("Invalid phone");
            }
        }
    }

    static class ContactIdentifierParser {

        public static ContactIdentifier parse(String value) {
            if (value.contains("@")) {
                return new EmailIdentifier(value);
            }
            return new PhoneIdentifier(value);
        }
    }
}