package com.example.demo.handler;

import com.example.demo.model.MailMessage;
import com.example.demo.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetHandler {


    @Autowired
    private MailService mailService;

    @KafkaListener(topics = "${kafka.mail.topic}" ,groupId = "mail-group")
    public void handle(MailMessage event,
                       Acknowledgment ack) {
        try {
            mailService.sendResetMail(event);
            ack.acknowledge();
        } catch (Exception e) {
            throw e; // 自动重试
        }

    }
}
