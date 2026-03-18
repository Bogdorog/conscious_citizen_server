package com.sergeev.conscious_citizen_server.media.internal.service;

import com.sergeev.conscious_citizen_server.media.api.FileStorage;
import com.sergeev.conscious_citizen_server.media.api.MediaApi;
import com.sergeev.conscious_citizen_server.media.api.dto.MediaAssetDto;
import com.sergeev.conscious_citizen_server.media.internal.mapper.MediaMapper;
import com.sergeev.conscious_citizen_server.media.internal.model.MediaAsset;
import com.sergeev.conscious_citizen_server.media.internal.repository.MediaAssetRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class MediaService implements MediaApi{

    private final FileStorage fileStorage;
    private final MediaAssetRepository mediaRepo;
    private final String downloadBase;
    private final MediaMapper mediaMapper;

    public MediaService(@Qualifier("resilientFileStorage") FileStorage fileStorage,
                        MediaAssetRepository mediaRepo,
                        @Value("${app.media.download-base:/api/media}") String downloadBase, MediaMapper mediaMapper) {
        this.fileStorage = fileStorage;
        this.mediaRepo = mediaRepo;
        this.downloadBase = downloadBase;
        this.mediaMapper = mediaMapper;
    }

    @Override
    @Transactional
    public CompletableFuture<MediaAssetDto> upload(MultipartFile file, Long ownerId, Long incidentId) {
        try {
            return fileStorage.save(file)
                    .thenApply(id -> {
                        try {
                            String checksum;
                            try (InputStream is = file.getInputStream()) {
                                checksum = DigestUtils.sha256Hex(is);
                            }

                            String dir1 = checksum.substring(0, 2);
                            String dir2 = checksum.substring(2, 4);
                            String baseName = checksum;
                            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                            String fileName = baseName + (ext != null && !ext.isBlank() ? "." + ext : "");
                            String filePath = Paths.get(dir1, dir2, baseName, fileName).toString();

                            Optional<MediaAsset> existing = mediaRepo.findById(id);
                            if (existing.isPresent()) {
                                return mediaMapper.toResponse(existing.get());
                            }

                            MediaAsset asset = new MediaAsset();
                            asset.setId(id);
                            asset.setOwnerId(ownerId);
                            asset.setIncidentId(incidentId);
                            asset.setFileName(file.getOriginalFilename());
                            asset.setFilePath(filePath);

                            mediaRepo.save(asset);

                            String downloadUrl = downloadBase + "/" + id + "/download";
                            return mediaMapper.toResponse(asset, downloadUrl);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (Exception e) {
            CompletableFuture<MediaAssetDto> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }

    @Override
    public MediaAssetDto getMeta(UUID id) {
        MediaAsset a = mediaRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        String url = downloadBase + "/" + id + "/download";
        return mediaMapper.toResponse(a, url);
    }

    @Override
    public InputStream download(UUID id) throws Exception {
        MediaAsset a = mediaRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        // build actual absolute path from storage root + filePath
        Path root = Paths.get(System.getProperty("user.dir")).resolve("media_storage").toAbsolutePath();
        Path path = root.resolve(a.getFilePath());
        if (!Files.exists(path)) throw new FileNotFoundException("File not found on disk");
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    @Override
    @Transactional
    public void delete(UUID id) throws Exception {
        MediaAsset a = mediaRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        Path root = Paths.get(System.getProperty("user.dir")).resolve("media_storage").toAbsolutePath();
        Path path = root.resolve(a.getFilePath());
        fileStorage.delete(path.toString());
        mediaRepo.deleteById(id);
    }

    public String buildDownloadUrl(UUID id) {
        return downloadBase + "/" + id + "/download";
    }

}
