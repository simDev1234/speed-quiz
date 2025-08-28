package com.example.ranking.global.util;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailSender {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String defaultFromAddress;

    @Async
    public void sendPlainEmail(String email, String title, String content) {
        sendEmailInternal(email, title, content, false);
    }

    @Async
    public void sendHtmlEmail(String email, String title, String htmlContent) {
        sendEmailInternal(email, title, htmlContent, true);
    }

    private void sendEmailInternal(String email, String title, String content, boolean isHtml) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(title);
            helper.setFrom(defaultFromAddress);
            helper.setText(content, isHtml);

            javaMailSender.send(message);
            log.info("Email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
