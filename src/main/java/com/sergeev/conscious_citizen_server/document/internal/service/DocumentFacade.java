package com.sergeev.conscious_citizen_server.document.internal.service;

import com.sergeev.conscious_citizen_server.incident.api.IncidentApi;
import com.sergeev.conscious_citizen_server.notification.api.EmailNotificationApi;
import com.sergeev.conscious_citizen_server.notification.api.dto.request.SendEmailWithAttachmentRequest;
import com.sergeev.conscious_citizen_server.user.api.UserApi;
import com.sergeev.conscious_citizen_server.user.api.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentFacade {

    private final DocumentService documentService;
    private final IncidentApi incidentApi;
    private final UserApi userApi;
    private final EmailNotificationApi emailApi;

    public void sendByEmail(Long incidentId) {
        byte[] file = documentService.download(incidentId);

        Long userId = incidentApi.getUserById(incidentId);
        UserDto user = userApi.getUserById(userId);

        emailApi.sendEmailWithAttachment(
                new SendEmailWithAttachmentRequest(
                        user.email(),
                        "Документ по инциденту #" + incidentId,
                        "<h3>Документ прикреплён к письму</h3>",
                        file,
                        "incident_" + incidentId + ".pdf"
                )
        );

        log.debug("Документ отправлен на {} для инцидента {}", user.email(), incidentId);
    }
}
