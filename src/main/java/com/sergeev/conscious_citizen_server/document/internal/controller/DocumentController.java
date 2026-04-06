package com.sergeev.conscious_citizen_server.document.internal.controller;

import com.sergeev.conscious_citizen_server.document.internal.service.DocumentFacade;
import com.sergeev.conscious_citizen_server.document.internal.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentFacade documentFacade;

    @PostMapping("/{id}/document")
    public ResponseEntity<Void> generate(@PathVariable Long id) {
        documentService.generateDocument(id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        byte[] file = documentService.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"incident_" + id + ".pdf\"")
                .body(file);
    }

    @GetMapping("/{id}/document/view")
    public ResponseEntity<byte[]> view(@PathVariable Long id) {
        byte[] file = documentService.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"incident_" + id + ".pdf\"")
                .body(file);
    }

    @PostMapping("/{id}/document/send")
    public ResponseEntity<Void> send(@PathVariable Long id) {
        documentFacade.sendByEmail(id);
        return ResponseEntity.ok().build();
    }
}