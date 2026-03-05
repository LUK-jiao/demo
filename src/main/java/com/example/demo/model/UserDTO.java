package com.example.demo.model;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;

    private String username;

    private String emailAddress;

    private String password;

    private int tokenVersion;
}
