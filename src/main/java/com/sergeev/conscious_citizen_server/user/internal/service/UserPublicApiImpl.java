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

    public Long registerUser(RegisterUserRequest request) {
        return service.register(request);
    }

    public UserDto getUser(String email) {
        return service.get(email);
    }

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
