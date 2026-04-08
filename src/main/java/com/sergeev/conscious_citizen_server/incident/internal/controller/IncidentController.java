package com.sergeev.conscious_citizen_server.incident.internal.controller;

import com.sergeev.conscious_citizen_server.incident.api.IncidentApi;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentAdminResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentShortResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.request.IncidentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentApi incidentApi;

    @PostMapping
    public IncidentResponse create(
            @RequestBody IncidentRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        return incidentApi.createIncident(request, userId);
    }

    @GetMapping
    public List<IncidentShortResponse> getAll() {
        return incidentApi.getAllIncidents();
    }

    @GetMapping("/count")
    public Integer getCount(@RequestHeader("X-User-Id") Long userId) {
        return incidentApi.getCount(userId);
    }

    @GetMapping("/drafts")
    public List<IncidentResponse> getAllDrafts(@RequestHeader("X-User-Id") Long userId) {
        return incidentApi.getAllDrafts(userId);
    }

    @GetMapping("/{id}")
    public IncidentResponse get(@PathVariable Long id) {
        return incidentApi.getIncidentById(id);
    }

    @GetMapping("/admin")
    public List<IncidentAdminResponse> getAllAdmin() {
        return incidentApi.getAllAdminIncidents();
    }

    @PutMapping("/{id}")
    public IncidentResponse updateIncident(
            @PathVariable Long id,
            @RequestBody IncidentRequest request,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return incidentApi.updateIncident(id, userId, request);
    }

    @DeleteMapping("/{id}")
    public void deleteIncident(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        incidentApi.deleteIncidentById(id, userId);
    }

}