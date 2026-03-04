package com.sergeev.conscious_citizen_server.user.internal.service;

import com.sergeev.conscious_citizen_server.user.api.event.PasswordResetRequestedEvent;
import com.sergeev.conscious_citizen_server.user.api.event.UserLoggedInEvent;
import com.sergeev.conscious_citizen_server.user.api.event.UserProfileUpdatedEvent;
import com.sergeev.conscious_citizen_server.user.api.event.UserRegisteredEvent;
import com.sergeev.conscious_citizen_server.user.internal.entity.PasswordResetToken;
import com.sergeev.conscious_citizen_server.user.internal.entity.User;
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
class UserService {

    private final UserRepository repository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordChangeService passwordChangeService;

    @Transactional
    public Long register(String fullName,
                         String email,
                         String phone,
                         String rawPassword) {

        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        String hash = passwordEncoder.encode(rawPassword);

        User user = repository.save(
                User.builder()
                        .fullName(fullName)
                        .email(email)
                        .phone(phone)
                        .passwordHash(hash)
                        .active(true)
                        .build()
        );

        publisher.publishEvent(new UserRegisteredEvent(user.getId()));

        return user.getId();
    }

    public Long login(String emailOrPhone, String rawPassword) {

        ContactIdentifier identifier =
                ContactIdentifierParser.parse(emailOrPhone);

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

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("User inactive");
        }

        publisher.publishEvent(new UserLoggedInEvent(user.getId()));

        return user.getId();
    }

    @Transactional
    public void updateProfile(Long id, String fullName, String phone) {

        User user = repository.findById(id)
                .orElseThrow();

        user.setFullName(fullName);
        user.setPhone(phone);

        publisher.publishEvent(new UserProfileUpdatedEvent(id));
    }

    public User get(Long id) {
        return repository.findById(id)
                .orElseThrow();
    }
    // Первый запрос о смене пароля
    @Transactional
    public void initiatePasswordReset(String email) {

        User user = repository.findByEmail(email)
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
    public void confirmPasswordReset(String token, String newPassword) {

        String tokenHash = passwordChangeService.hashToken(token);

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

        user.setPasswordHash(passwordEncoder.encode(newPassword));
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