package com.sergeev.conscious_citizen_server.notification.api;

import com.sergeev.conscious_citizen_server.notification.api.dto.request.SendEmailRequest;
import com.sergeev.conscious_citizen_server.notification.api.dto.request.SendEmailWithAttachmentRequest;

public interface EmailNotificationApi {

    void sendEmail(SendEmailRequest request);

    void sendEmailWithAttachment(SendEmailWithAttachmentRequest request);

}
