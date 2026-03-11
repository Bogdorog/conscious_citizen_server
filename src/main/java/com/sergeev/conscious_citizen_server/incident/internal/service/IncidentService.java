package com.sergeev.conscious_citizen_server.incident.internal.service;

import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.request.IncidentRequest;
import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import com.sergeev.conscious_citizen_server.incident.internal.mapper.IncidentMapper;
import com.sergeev.conscious_citizen_server.incident.internal.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository repository;
    private final NominatimService nominatimService;
    private final IncidentMapper mapper;

    public IncidentResponse createIncident(IncidentRequest request, Long userId) {

        Incident incident = new Incident();

        incident.setTitle(request.title());
        incident.setMessage(request.description());
        incident.setLatitude(request.latitude());
        incident.setLongitude(request.longitude());

        String address = nominatimService.reverse(
                request.latitude(),
                request.latitude()
        );

        incident.setAddress(address);
        incident.setUserId(userId);

        Incident saved = repository.save(incident);

        return mapper.toDto(saved);
    }

    public List<IncidentResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

}
