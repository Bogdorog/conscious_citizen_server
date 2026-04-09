package com.sergeev.conscious_citizen_server.user.api;

import com.sergeev.conscious_citizen_server.user.api.dto.AuthResult;
import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;
import com.sergeev.conscious_citizen_server.user.api.dto.UsersForAdmin;
import com.sergeev.conscious_citizen_server.user.api.dto.request.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    CompletableFuture<UserDto> uploadAvatar(Long userId, MultipartFile file) throws Exception;

    UserDto deleteAvatar(Long userId);
}
