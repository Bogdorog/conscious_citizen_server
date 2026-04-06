package com.sergeev.conscious_citizen_server.exception;

public class IncidentNotFoundException extends RuntimeException {
    public IncidentNotFoundException(Long id) {
        super("Incident not found: " + id);
    }
}
