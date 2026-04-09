package com.sergeev.conscious_citizen_server.incident.internal.service;

import com.sergeev.conscious_citizen_server.incident.internal.entity.Incident;
import com.sergeev.conscious_citizen_server.incident.internal.repository.IncidentRepository;
import com.sergeev.conscious_citizen_server.media.api.MediaApi;
import com.sergeev.conscious_citizen_server.media.api.dto.MediaAssetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class IncidentMediaService {

    private final IncidentRepository incidentRepository;
    private final MediaApi mediaApi;

    public CompletableFuture<MediaAssetDto> uploadPhoto(Long incidentId,
                                                        MultipartFile file,
                                                        Long userId) throws Exception{

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Инцидент не найден"));

        // 🔐 проверка владельца
        if (!incident.getUserId().equals(userId)) {
            throw new RuntimeException("Доступ запрещен");
        }

        return mediaApi.upload(file, userId, incidentId);
    }
}
