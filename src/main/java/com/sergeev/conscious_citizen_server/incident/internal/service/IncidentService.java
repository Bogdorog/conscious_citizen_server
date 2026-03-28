package com.sergeev.conscious_citizen_server.incident.internal.service;

import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentShortResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.request.IncidentRequest;
import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import com.sergeev.conscious_citizen_server.incident.internal.mapper.IncidentMapper;
import com.sergeev.conscious_citizen_server.incident.internal.repository.IncidentRepository;
import com.sergeev.conscious_citizen_server.incident.internal.repository.IncidentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository repository;
    private final NominatimService nominatimService;
    private final IncidentMapper mapper;
    private final IncidentTypeRepository typeRepository;

    //@Cacheable(value = "incident-map")
    public List<IncidentShortResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toShortDto)
                .toList();
    }

    // 📌 ДЕТАЛЬНЫЙ ИНЦИДЕНТ
    //@Cacheable(value = "incident-details", key = "#id")
    public IncidentResponse getById(Long id) {

        Incident incident = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        return mapper.toDto(incident);
    }

    public IncidentResponse createIncident(IncidentRequest request, Long userId) {

        Incident incident = new Incident();

        incident.setTitle(request.title());
        incident.setMessage(request.description());
        incident.setLatitude(request.latitude());
        incident.setLongitude(request.longitude());
        if (request.address().isBlank())
        {
            String address = nominatimService.reverse(
                    request.latitude(),
                    request.longitude()
            );
            incident.setAddress(address);
        }
        else incident.setAddress(request.address());
        incident.setType(typeRepository.findByName(request.type()));
        incident.setUserId(userId);
        incident.setActive(request.active());

        Incident saved = repository.save(incident);

        return mapper.toDto(saved);
    }

}
