package com.sergeev.conscious_citizen_server.incident.internal.repository;

import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long>
{}
