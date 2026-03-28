package com.sergeev.conscious_citizen_server.user.internal.mapper;

import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;
import com.sergeev.conscious_citizen_server.user.internal.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "login", source = "username")
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "role", source = "role", ignore = true)
    User toEntity(UserDto dto);

    @Mapping(target = "role", expression = "java(user.getRole().getName())")
    @Mapping(target = "username", source = "login")
    UserDto toResponse(User user);
}
