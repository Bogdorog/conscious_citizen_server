package com.sergeev.conscious_citizen_server.user.internal;

import com.sergeev.conscious_citizen_server.user.api.UserApi;
import com.sergeev.conscious_citizen_server.user.api.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserPublicApiImpl implements UserApi {

    private final UserService service;

    @Override
    public Long registerUser(String fullName, String email, String phone) {
        return service.register(fullName, email, phone);
    }

    @Override
    public UserDto getUser(Long id) {
        User user = service.get(id);
        return new UserDto(
                user.getId(),
                user.getFullName(),
                user.getAddress(),
                user.getEmail(),
                user.getPhone()
        );
    }
}
