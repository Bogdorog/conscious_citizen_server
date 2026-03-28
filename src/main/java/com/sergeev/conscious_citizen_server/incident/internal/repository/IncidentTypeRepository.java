package com.sergeev.conscious_citizen_server.incident.internal.repository;

import com.sergeev.conscious_citizen_server.incident.internal.entity.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentTypeRepository extends JpaRepository<IncidentType, Long> {
    IncidentType findByName(String name);
}
