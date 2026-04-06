package com.sergeev.conscious_citizen_server.incident.internal.service;

import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentAdminResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.IncidentShortResponse;
import com.sergeev.conscious_citizen_server.incident.api.dto.request.IncidentRequest;
import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import com.sergeev.conscious_citizen_server.incident.internal.mapper.IncidentMapper;
import com.sergeev.conscious_citizen_server.incident.internal.repository.IncidentRepository;
import com.sergeev.conscious_citizen_server.incident.internal.repository.IncidentTypeRepository;
import com.sergeev.conscious_citizen_server.user.api.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository repository;
    private final NominatimService nominatimService;
    private final IncidentMapper mapper;
    private final IncidentTypeRepository typeRepository;
    private final UserApi userApi;

    //@Cacheable(value = "incident-map")
    public List<IncidentShortResponse> getAll() {

        return repository.findAllByActiveTrue()
                .stream()
                .map(mapper::toShortDto)
                .toList();
    }

    public List<IncidentAdminResponse> getAllAdmin() {

        return repository.findAllByActiveTrue()
                .stream()
                .map(mapper::toAdminDto)
                .toList();
    }

    public List<IncidentResponse> getAllDrafts(Long userId) {

        return repository.findAllByActiveFalseAndUserId(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    //@Cacheable(value = "incident-details", key = "#id")
    public IncidentResponse getById(Long id) {

        Incident incident = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Инцидент не найден"));

        return mapper.toDto(incident);
    }

    public Long getUserById(Long id) {

        Incident incident = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Инцидент не найден"));

        return incident.getUserId();
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

        incident.setCreatedAt(LocalDateTime.now());

        Incident saved = repository.save(incident);

        return mapper.toDto(saved);
    }

    @Transactional
    public IncidentResponse updateIncident(Long incidentId,
                                   Long userId,
                                   IncidentRequest request) {

        Incident incident = repository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Инцидент не найден"));

        // проверка владельца
        if (!incident.getUserId().equals(userId)  && userApi.getRole(userId).equals("ADMIN") ) {
            throw new RuntimeException("Доступ запрещен");
        }

        // обновляем только если не null
        if (request.title() != null) {
            incident.setTitle(request.title());
        }

        if (request.description() != null) {
            incident.setMessage(request.description());
        }

        if (request.latitude() != null) {
            incident.setLatitude(request.latitude());
        }

        if (request.longitude() != null) {
            incident.setLongitude(request.longitude());
        }

        if (request.address() != null) {
            incident.setAddress(request.address());
        }

        if (request.type() != null) {
            incident.setType(typeRepository.findByName(request.type()));
        }

        Incident saved = repository.save(incident);

        return mapper.toDto(saved);
    }

    @Transactional
    public void deleteIncident(Long incidentId, Long userId) {

        Incident incident = repository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Инцидент не найден"));

        // Проверка владельца
        if (!incident.getUserId().equals(userId) && userApi.getRole(userId).equals("ADMIN") ) {
            throw new RuntimeException("Доступ запрещен");
        }

        repository.delete(incident);
    }
}
