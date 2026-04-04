package com.sergeev.conscious_citizen_server.incident.api;

import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentAdminResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentShortResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.request.IncidentRequest;

import java.util.List;

public interface IncidentApi {
    IncidentResponse createIncident(IncidentRequest request, Long userId);

    List<IncidentShortResponse> getAllIncidents();

    List<IncidentAdminResponse> getAllAdminIncidents();

    IncidentResponse getIncidentById(Long id);

    List<IncidentResponse> getAllDrafts(Long userId);
}
