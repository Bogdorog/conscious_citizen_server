package com.sergeev.conscious_citizen_server.incident.internal.service;

import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import com.sergeev.conscious_citizen_server.incident.internal.repository.IncidentRepository;
import com.sergeev.conscious_citizen_server.media.api.MediaApi;
import com.sergeev.conscious_citizen_server.media.api.dto.MediaAssetDto;
import com.sergeev.conscious_citizen_server.user.api.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class IncidentMediaService {

    private final IncidentRepository incidentRepository;
    private final MediaApi mediaApi;
    private final UserApi userApi;

    @CacheEvict(value = {"incident-map", "incident-details"}, allEntries = true)
    public CompletableFuture<MediaAssetDto> uploadPhoto(Long incidentId,
                                                        MultipartFile file,
                                                        Long userId) throws Exception{

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Инцидент не найден"));
        // Проверка прав: только владелец инцидента или админ
        if (!incident.getUserId().equals(userId)  && !userApi.getRole(userId).equals("ADMIN") ) {
            throw new RuntimeException("Доступ запрещен");
        }

        return mediaApi.upload(file, userId, incidentId);
    }

    @Transactional
    @CacheEvict(value = {"incident-map", "incident-details"}, allEntries = true)
    public void removePhotoFromIncident(Long incidentId, UUID mediaAssetId, Long userId) throws Exception {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Инцидент не найден"));

        // Проверка прав: только владелец инцидента или админ
        if (!incident.getUserId().equals(userId) && !userApi.getRole(userId).equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещён");
        }

        mediaApi.delete(mediaAssetId, incidentId);
    }

    // Полное удаление фото для админов (на будущее)
    @Transactional
    @CacheEvict(value = {"incident-map", "incident-details"}, allEntries = true)
    public void deletePhotoCompletely(UUID mediaAssetId, Long userId) {
        // Проверка прав администратора
        if (!userApi.getRole(userId).equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Требуется роль администратора");
        }

        mediaApi.deleteCompletely(mediaAssetId);
    }
}
