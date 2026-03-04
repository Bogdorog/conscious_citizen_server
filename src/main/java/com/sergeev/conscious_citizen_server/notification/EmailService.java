package com.sergeev.conscious_citizen_server.notification;

// Ранняя версия для смены пароля
public class EmailService {
    /* Здесь должна быть реализация отправки письма с ссылкой для смены пароля и тд но ее пока нет
    @ApplicationModuleListener
    void on(PasswordResetRequestedEvent event) {

        String link = "https://нашдомен.ру/reset-password?token=" + event.token();

        emailService.send(
                event.email(),
                "Reset your password",
                "Click here: " + link
        );
    }
    */
}
