package com.sergeev.conscious_citizen_server.notification.internal.service;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    public String buildResetPasswordEmail(String link) {
        return """
                <html>
                    <body>
                        <h2>Password reset</h2>
                        <p>Click the link below:</p>
                        <a href="%s">Reset password</a>
                    </body>
                </html>
                """.formatted(link);
    }

}
