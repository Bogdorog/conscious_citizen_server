package com.sergeev.conscious_citizen_server.incident.internal.mapper;

import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentAdminResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentShortResponse;
import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import com.sergeev.conscious_citizen_server.user.api.UserApi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class IncidentMapper {
    @Autowired
    protected UserApi userApi;
    @Mapping(target = "description", source = "message")
    @Mapping(target = "type", expression = "java(incident.getType().getName())")
    @Mapping(target = "created", source = "createdAt")
    @Mapping(target = "fullName", expression = "java(userApi.getUserById(incident.getUserId()).fullName())")
    public abstract IncidentResponse toDto(Incident incident);
    @Mapping(target = "type", expression = "java(incident.getType().getName())")
    @Mapping(target = "created", source = "createdAt")
    public abstract IncidentShortResponse toShortDto(Incident incident);
    @Mapping(target = "type", expression = "java(incident.getType().getName())")
    @Mapping(target = "created", source = "createdAt")
    @Mapping(target = "fullName", expression = "java(userApi.getUserById(incident.getUserId()).fullName())")
    public abstract IncidentAdminResponse toAdminDto(Incident incident);
}
