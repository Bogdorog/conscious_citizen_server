package com.sergeev.conscious_citizen_server.user.internal;

import com.sergeev.conscious_citizen_server.user.api.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class UserService {

    private final UserRepository repository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;

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

    @Transactional
    public void requestPasswordReset(String emailOrPhone) {

        User user = repository.findByEmail(emailOrPhone)
                .or(() -> repository.findByPhone(emailOrPhone))
                .orElseThrow();

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiration(LocalDateTime.now().plusMinutes(5));

        publisher.publishEvent(
                new PasswordResetRequestedEvent(user.getId(), token)
        );
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {

        User user = repository.findByResetToken(token)
                .orElseThrow();

        if (user.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);

        publisher.publishEvent(new PasswordChangedEvent(user.getId()));
    }
}

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

class ContactIdentifierParser {

    public static ContactIdentifier parse(String value) {
        if (value.contains("@")) {
            return new EmailIdentifier(value);
        }
        return new PhoneIdentifier(value);
    }
}