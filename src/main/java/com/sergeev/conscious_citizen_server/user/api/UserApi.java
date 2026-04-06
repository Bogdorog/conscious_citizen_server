package com.sergeev.conscious_citizen_server.user.api;

import com.sergeev.conscious_citizen_server.user.api.dto.AuthResult;
import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;
import com.sergeev.conscious_citizen_server.user.api.dto.UsersForAdmin;
import com.sergeev.conscious_citizen_server.user.api.dto.request.*;

import java.util.List;

public interface UserApi {

    void registerUser(RegisterUserRequest request);

    AuthResult login(LoginRequest request);

    void initiatePasswordReset(PasswordResetRequest request);

    void confirmPasswordReset(PasswordResetConfirmRequest request);

    UserDto updateProfile(UpdateProfileRequest request);

    UserDto getUser(String login);

    UserDto getUserById(Long id);

    String getRole(String login);

    String getRole(Long id);

    List<UsersForAdmin> getUsersForAdmin();
}
