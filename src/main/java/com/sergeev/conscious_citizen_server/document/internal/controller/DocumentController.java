package com.sergeev.conscious_citizen_server.document.internal.controller;

import com.sergeev.conscious_citizen_server.document.internal.service.DocumentService;
import com.sergeev.conscious_citizen_server.incident.api.IncidentApi;
import com.sergeev.conscious_citizen_server.notification.api.EmailNotificationApi;
import com.sergeev.conscious_citizen_server.notification.api.dto.request.SendEmailWithAttachmentRequest;
import com.sergeev.conscious_citizen_server.user.api.UserApi;
import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final EmailNotificationApi emailApi;
    private final UserApi userApi;
    private final IncidentApi incidentApi;

    @PostMapping("/{id}/document")
    public void generate(@PathVariable Long id) {
        documentService.generateDocument(id);
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {

        byte[] file = documentService.download(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=document.pdf")
                .body(file);
    }

    @GetMapping("/{id}/document/view")
    public ResponseEntity<byte[]> view(@PathVariable Long id) {

        byte[] file = documentService.download(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=document.pdf")
                .header("Content-Type", "application/pdf")
                .body(file);
    }

    @PostMapping("/{id}/document/send")
    public void send(@PathVariable Long id) {

        byte[] file = documentService.download(id);
        UserDto user = userApi.getUserById(incidentApi.getUserById(id));

        emailApi.sendEmailWithAttachment(
                new SendEmailWithAttachmentRequest(
                        user.email(),
                        "Your document",
                        "<h3>See attachment</h3>",
                        file,
                        "incident.pdf"
                )
        );
    }
}