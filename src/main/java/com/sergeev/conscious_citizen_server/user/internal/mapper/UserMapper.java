package com.sergeev.conscious_citizen_server.user.internal.mapper;

import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;
import com.sergeev.conscious_citizen_server.user.internal.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    User toEntity(UserDto dto);

    //@Mapping(target = "roles", expression = "java(user.getRole().getName())")
    UserDto toResponse(User user);
}
