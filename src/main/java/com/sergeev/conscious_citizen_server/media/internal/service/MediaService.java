package com.sergeev.conscious_citizen_server.media.internal.service;

import com.sergeev.conscious_citizen_server.media.api.FileStorage;
import com.sergeev.conscious_citizen_server.media.api.MediaApi;
import com.sergeev.conscious_citizen_server.media.api.dto.MediaAssetDto;
import com.sergeev.conscious_citizen_server.media.internal.mapper.MediaMapper;
import com.sergeev.conscious_citizen_server.media.internal.model.MediaAsset;
import com.sergeev.conscious_citizen_server.media.internal.repository.MediaAssetRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class MediaService implements MediaApi{

    private final FileStorage fileStorage;
    private final MediaAssetRepository mediaRepo;
    private final String downloadBase;
    private final MediaMapper mediaMapper;
    private final TransactionTemplate tx;
    private final Path storageRoot;

    public MediaService(
            @Qualifier("resilientFileStorage") FileStorage fileStorage,
            MediaAssetRepository mediaRepo,
            @Value("${app.media.download-base:/api/media}") String downloadBase,
            MediaMapper mediaMapper,
            TransactionTemplate tx,
            FileSystemStorage fileSystemStorage
    ) {
        this.fileStorage   = fileStorage;
        this.mediaRepo     = mediaRepo;
        this.downloadBase  = downloadBase;
        this.mediaMapper   = mediaMapper;
        this.tx            = tx;
        this.storageRoot   = fileSystemStorage.getStorageRoot();
    }

    @Override
    public CompletableFuture<MediaAssetDto> upload(MultipartFile file, Long ownerId, Long incidentId) throws Exception {
        return fileStorage.save(file)
                .thenApply(result -> tx.execute(status -> {
                    // Дубликат? — обновляем контекстные поля и сохраняем
                    return mediaRepo.findById(result.id())
                            .map(existing -> {
                                existing.setOwnerId(ownerId);
                                existing.setIncidentId(incidentId);
                                mediaRepo.save(existing);
                                return mediaMapper.toResponse(existing, buildDownloadUrl(existing.getId()));
                            })
                            .orElseGet(() -> {
                                MediaAsset asset = new MediaAsset();
                                asset.setId(result.id());
                                asset.setOwnerId(ownerId);
                                asset.setIncidentId(incidentId);
                                asset.setFileName(file.getOriginalFilename());
                                asset.setFilePath(result.filePath());
                                mediaRepo.save(asset);
                                return mediaMapper.toResponse(asset, buildDownloadUrl(asset.getId()));
                            });
                }));
    }

    @Override
    public MediaAssetDto getMeta(UUID id) {
        MediaAsset a = mediaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Медиафайл не найден"));
        return mediaMapper.toResponse(a, buildDownloadUrl(id));
    }

    @Override
    public Long getSize(MediaAssetDto a) {
        MediaAsset asset = mediaRepo.findById(a.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Медиафайл не найден"));

        Path path = storageRoot.resolve(asset.getFilePath()).normalize();
        if (!Files.exists(path))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл отсутствует на диске");
        return path.toFile().length();
    }

    @Override
    public InputStream download(UUID id) throws IOException {
        MediaAsset a = mediaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Медиафайл не найден"));

        Path path = storageRoot.resolve(a.getFilePath()).normalize();
        if (!Files.exists(path))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл отсутствует на диске");

        return Files.newInputStream(path, StandardOpenOption.READ);
    }

        @Override
    public void delete(UUID id) throws Exception {
        MediaAsset a = mediaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Медиафайл не найден"));

        // Транзакция: сначала удаляем из БД, потом с диска
        tx.executeWithoutResult(status -> mediaRepo.deleteById(id));
        fileStorage.delete(a.getFilePath()); // async, ошибка диска не откатит БД — приемлемо
    }

    public String buildDownloadUrl(UUID id) {
        return downloadBase + "/" + id + "/download";
    }

    @Override
    public List<MediaAssetDto> findByIncidentId(Long incidentId) {
        return mediaRepo.findByIncidentId(incidentId)
                .stream()
                .map(a -> mediaMapper.toResponse(a, buildDownloadUrl(a.getId())))
                .toList();
    }

}
