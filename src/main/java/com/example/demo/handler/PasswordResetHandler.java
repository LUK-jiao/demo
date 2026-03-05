package com.example.demo.handler;

import com.example.demo.model.MailMessage;
import com.example.demo.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

@Slf4j
@Component
public class PasswordResetHandler {


    @Autowired
    private MailService mailService;

    @KafkaListener(topics = "${kafka.mail.topic}" ,groupId = "mail-group")
    public void handle(@Payload MailMessage event) {
        try {
            mailService.sendResetMail(event);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", event.getEmailAddress(), e);
        }

    }
}
