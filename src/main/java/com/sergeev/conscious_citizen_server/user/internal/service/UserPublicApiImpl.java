package com.sergeev.conscious_citizen_server.user.internal.service;

import com.sergeev.conscious_citizen_server.user.api.UserApi;
import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;
import com.sergeev.conscious_citizen_server.user.internal.entity.User;
import com.sergeev.conscious_citizen_server.user.internal.mapper.UserMapper;
import com.sergeev.conscious_citizen_server.user.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserPublicApiImpl implements UserApi {

    private final UserService service;
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public Long registerUser(String fullName, String email, String phone, String password) {
        return service.register(fullName, email, phone, password);
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

    @Override
    public UserDto getUserByEmail(String email) {
        User user = repository.findByEmail(email).get();
        return mapper.toResponse(user);
    }

    @Override
    public Long login(String email, String rawPassword) {
        return service.login(email, rawPassword);
    }

    @Override
    public void initiatePasswordReset(String emailOrPhone) {
        service.initiatePasswordReset(emailOrPhone);
    }

    @Override
    public void confirmPasswordReset(String token, String newPassword) {
        service.confirmPasswordReset(token, newPassword);
    }
/*
    @Override
    public void updateProfile(UpdateProfileRequest request) {
        service.updateProfile(request);
    }*/
}
