package com.sergeev.conscious_citizen_server.media.internal.controller;

import com.sergeev.conscious_citizen_server.media.api.MediaApi;
import com.sergeev.conscious_citizen_server.media.api.dto.MediaAssetDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaApi mediaApi;

    @GetMapping("/{id}/meta")
    public MediaAssetDto getMeta(@PathVariable UUID id) {
        return mediaApi.getMeta(id);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<StreamingResponseBody> download(@PathVariable UUID id) throws IOException {
        MediaAssetDto meta = mediaApi.getMeta(id);

        // Определяем Content-Type по имени файла из метаданных
        String filename  = meta.fileName() != null ? meta.fileName() : "file";
        MediaType contentType = resolveMediaType(filename);

        StreamingResponseBody body = out -> {
            try (InputStream in = mediaApi.download(id)) {
                in.transferTo(out);
            }
            catch (Exception e) {}
        };

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) throws Exception {
        mediaApi.delete(id);
    }

    private MediaType resolveMediaType(String filename) {
        String ext = FilenameUtils.getExtension(filename).toLowerCase();
        return switch (ext) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png"         -> MediaType.IMAGE_PNG;
            case "webp"        -> MediaType.parseMediaType("image/webp");
            default            -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
