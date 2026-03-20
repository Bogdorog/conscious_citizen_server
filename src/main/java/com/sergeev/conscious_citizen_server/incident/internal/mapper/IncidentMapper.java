package com.sergeev.conscious_citizen_server.incident.internal.mapper;

import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentShortResponse;
import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IncidentMapper {
    IncidentResponse toDto(Incident incident);
    IncidentShortResponse toShortDto(Incident incident);
    Incident toEntity(IncidentResponse dto);
}
