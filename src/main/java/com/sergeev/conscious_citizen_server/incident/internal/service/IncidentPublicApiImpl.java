package com.sergeev.conscious_citizen_server.incident.internal.service;

import com.sergeev.conscious_citizen_server.incident.api.IncidentApi;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentAdminResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentShortResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.request.IncidentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IncidentPublicApiImpl implements IncidentApi {

    private final IncidentService incidentService;

    @Override
    public IncidentResponse createIncident(IncidentRequest request, Long userId) {
        return incidentService.createIncident(request, userId);
    }

    @Override
    public List<IncidentShortResponse> getAllIncidents() {
        return incidentService.getAll();
    }

    @Override
    public List<IncidentAdminResponse> getAllAdminIncidents() {
        return incidentService.getAllAdmin();
    }

    @Override
    public List<IncidentResponse> getAllDrafts(Long userId) {
        return incidentService.getAllDrafts(userId);
    }

    @Override
    public IncidentResponse getIncidentById(Long id) {
        return incidentService.getById(id);
    }

    @Override
    public Long getUserById(Long id) {
        return incidentService.getUserById(id);
    }
}
