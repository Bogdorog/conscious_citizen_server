package com.sergeev.conscious_citizen_server.user.api;

import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;

public interface UserApi {

    Long registerUser(String fullName, String email, String phone, String password);

    Long login(String email, String rawPassword);

    void initiatePasswordReset(String emailOrPhone);

    void confirmPasswordReset(String token, String newPassword);

    //void updateProfile(UpdateProfileRequest request);

    UserDto getUser(Long id);
}
