package com.sergeev.conscious_citizen_server.user.api;

import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;
import com.sergeev.conscious_citizen_server.user.api.dto.request.*;

public interface UserApi {

    Long registerUser(RegisterUserRequest request);

    Long login(LoginRequest request);

    void initiatePasswordReset(PasswordResetRequest request);

    void confirmPasswordReset(PasswordResetConfirmRequest request);

    UserDto updateProfile(UpdateProfileRequest request);

    UserDto getUser(String email);
}
