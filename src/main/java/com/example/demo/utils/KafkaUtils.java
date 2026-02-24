package com.example.demo.utils;

import com.example.demo.model.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaUtils {

    @Autowired
    private KafkaTemplate<String, PasswordResetToken> kafkaTemplate;

    @Value("${kafka.mail.topic}")
    private String mailTopic;

    public void send(PasswordResetToken passwordResetToken) {
        kafkaTemplate.send(mailTopic,passwordResetToken.getId().toString(),passwordResetToken);
    }
}
