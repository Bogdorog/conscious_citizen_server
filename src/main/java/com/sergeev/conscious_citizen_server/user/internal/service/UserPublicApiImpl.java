package com.sergeev.conscious_citizen_server.user.internal.service;

import com.sergeev.conscious_citizen_server.user.api.UserApi;
import com.sergeev.conscious_citizen_server.user.api.dto.AuthResult;
import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;
import com.sergeev.conscious_citizen_server.user.api.dto.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserPublicApiImpl implements UserApi {

    private final UserService service;

    public void registerUser(RegisterUserRequest request) {
        service.register(request);
    }

    public UserDto getUser(String login) {
        return service.get(login);
    }

    public UserDto getUserById(Long id) { return service.getById(id); }

    public String getRole(String login) {return service.getRole(login);}

    public AuthResult login(LoginRequest request) {
        return service.login(request);
    }

    public void initiatePasswordReset(PasswordResetRequest request) {
        service.initiatePasswordReset(request);
    }

    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        service.confirmPasswordReset(request);
    }

    public UserDto updateProfile(UpdateProfileRequest request) {
        return service.updateProfile(request);
    }
}
