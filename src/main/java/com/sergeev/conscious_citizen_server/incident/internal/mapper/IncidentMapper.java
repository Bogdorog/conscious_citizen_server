package com.sergeev.conscious_citizen_server.incident.internal.mapper;

import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentShortResponse;
import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IncidentMapper {
    @Mapping(target = "description", source = "message")
    @Mapping(target = "type", expression = "java(incident.getType().getName())")
    IncidentResponse toDto(Incident incident);
    @Mapping(target = "type", expression = "java(incident.getType().getName())")
    @Mapping(target = "created", source = "createdAt")
    IncidentShortResponse toShortDto(Incident incident);
}
