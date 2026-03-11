package com.sergeev.conscious_citizen_server.incident.api.dto.request;

public record IncidentRequest(
        String title,
        String description,
        double latitude,
        double longitude
) {}
