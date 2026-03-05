package com.example.demo.model;

import lombok.Data;

@Data
public class MailMessage {

    private String emailAddress;

    private PasswordResetToken passwordResetToken;
}
