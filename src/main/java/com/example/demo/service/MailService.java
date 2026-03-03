package com.example.demo.service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.MailMessage;
import com.example.demo.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailService {

    @Autowired
    private MailUtils mailUtils;

    public void sendResetMail(MailMessage event) {
        String to = event.getEamil_address();
        String subject = "带有token的忘记密码邮件";
        String content = String.format("token : %s", event.getPasswordResetToken().getTokenHash());
        mailUtils.sendSimpleMail(to, subject, content);
    }
}
