package com.sergeev.conscious_citizen_server.notification.internal.service;

import com.sergeev.conscious_citizen_server.notification.api.EmailNotificationApi;
import com.sergeev.conscious_citizen_server.notification.api.dto.request.SendEmailRequest;
import com.sergeev.conscious_citizen_server.user.api.event.PasswordResetRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResetListener {

    private final EmailNotificationApi emailApi;
    private final EmailTemplateService emailTemplateService;
    @Value("${server.port}")
    private int PORT;

    @ApplicationModuleListener
    public void handle(PasswordResetRequestedEvent event) {

        String link = "http://localhost:" + PORT + "/reset?token=" + event.token();

        String html = emailTemplateService.buildResetPasswordEmail(link);

        emailApi.sendEmail(
                new SendEmailRequest(
                        event.email(),
                        "Reset password",
                        html
                )
        );
    }
}
