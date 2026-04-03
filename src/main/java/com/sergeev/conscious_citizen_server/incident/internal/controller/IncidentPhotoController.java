package com.sergeev.conscious_citizen_server.incident.internal.controller;

import com.sergeev.conscious_citizen_server.incident.internal.service.IncidentMediaService;
import com.sergeev.conscious_citizen_server.media.api.MediaApi;
import com.sergeev.conscious_citizen_server.media.api.dto.MediaAssetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/incidents/{incidentId}/photos")
@RequiredArgsConstructor
public class IncidentPhotoController {

    private final MediaApi mediaApi;
    private final IncidentMediaService service;

    @PostMapping
    public CompletableFuture<MediaAssetDto> upload(
            @PathVariable Long incidentId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return service.uploadPhoto(incidentId, file, userId);
    }

    @GetMapping
    public List<MediaAssetDto> getIncidentPhotos(@PathVariable Long incidentId) {
        return mediaApi.findByIncidentId(incidentId);
    }
}
