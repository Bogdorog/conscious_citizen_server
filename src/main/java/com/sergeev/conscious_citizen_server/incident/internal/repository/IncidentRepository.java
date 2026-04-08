package com.sergeev.conscious_citizen_server.incident.internal.repository;

import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long>
{
    List<Incident> findAllByActiveTrue();

    List<Incident> findAllByActiveFalseAndUserId(Long userId);

    Integer countByUserId(Long userId);
}
