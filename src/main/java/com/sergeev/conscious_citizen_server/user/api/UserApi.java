package com.sergeev.conscious_citizen_server.user.api;

public interface UserApi {

    Long registerUser(String fullName, String email, String phone);

    UserDto getUser(Long id);
}
