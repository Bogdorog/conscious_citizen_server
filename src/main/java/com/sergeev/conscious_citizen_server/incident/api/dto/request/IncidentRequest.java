package com.sergeev.conscious_citizen_server.incident.api.dto.request;

public record IncidentRequest(
        String title,
        String description,
        String type,
        String address,
        Double latitude,
        Double longitude,
        boolean active
) {}
