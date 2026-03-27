package com.sergeev.conscious_citizen_server.notification.internal.service;

import com.sergeev.conscious_citizen_server.notification.internal.exception.EmailSendingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void send(String to, String subject, String text) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    @Async
    public void sendWithAttachment(String to,
                                   String subject,
                                   String text,
                                   byte[] file,
                                   String filename) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            helper.addAttachment(filename, new ByteArrayResource(file));

            mailSender.send(message);

        } catch (Exception e) {
            throw new EmailSendingException("Failed to send email with attachment", e);
        }
    }
}
