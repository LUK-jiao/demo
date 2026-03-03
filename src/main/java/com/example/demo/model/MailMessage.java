package com.example.demo.model;

import lombok.Data;

@Data
public class MailMessage {

    private String eamil_address;

    private PasswordResetToken passwordResetToken;
}
