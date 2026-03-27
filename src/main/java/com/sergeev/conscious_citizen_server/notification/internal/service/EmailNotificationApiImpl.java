package com.sergeev.conscious_citizen_server.notification.internal.service;

import com.sergeev.conscious_citizen_server.notification.api.EmailNotificationApi;
import com.sergeev.conscious_citizen_server.notification.api.dto.request.SendEmailRequest;
import com.sergeev.conscious_citizen_server.notification.api.dto.request.SendEmailWithAttachmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationApiImpl implements EmailNotificationApi {

    private final EmailService emailService;

    @Override
    public void sendEmail(SendEmailRequest request) {
        emailService.send(
                request.to(),
                request.subject(),
                request.text()
        );
    }

    @Override
    public void sendEmailWithAttachment(SendEmailWithAttachmentRequest request) {
        emailService.sendWithAttachment(
                request.to(),
                request.subject(),
                request.text(),
                request.file(),
                request.filename()
        );
    }
}
