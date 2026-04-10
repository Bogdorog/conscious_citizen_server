package com.sergeev.conscious_citizen_server.incident.internal.controller;

import com.sergeev.conscious_citizen_server.incident.internal.service.IncidentMediaService;
import com.sergeev.conscious_citizen_server.media.api.MediaApi;
import com.sergeev.conscious_citizen_server.media.api.dto.MediaAssetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
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
    ) throws Exception {
        return service.uploadPhoto(incidentId, file, userId);
    }

    @GetMapping
    public List<MediaAssetDto> getIncidentPhotos(@PathVariable Long incidentId) {
        return mediaApi.findByIncidentId(incidentId);
    }

    @DeleteMapping("/{mediaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromIncident(
            @PathVariable Long incidentId,
            @PathVariable UUID mediaId,
            @RequestHeader("X-User-Id") Long userId
    ) throws Exception {
        service.removePhotoFromIncident(incidentId, mediaId, userId);
    }

    @DeleteMapping("/{mediaId}/force")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompletely(
            @PathVariable UUID mediaId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        service.deletePhotoCompletely(mediaId, userId);
    }
}
