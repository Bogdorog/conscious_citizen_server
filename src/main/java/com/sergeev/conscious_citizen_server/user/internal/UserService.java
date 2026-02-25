package com.sergeev.conscious_citizen_server.user.internal;

import com.sergeev.conscious_citizen_server.user.api.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UserService {

    private final UserRepository repository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public Long register(String fullName, String email, String phone) {

        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = repository.save(
                User.builder()
                        .fullName(fullName)
                        .email(email)
                        .phone(phone)
                        .build()
        );

        publisher.publishEvent(new UserRegisteredEvent(user.getId()));

        return user.getId();
    }

    public User get(Long id) {
        return repository.findById(id)
                .orElseThrow();
    }
}
