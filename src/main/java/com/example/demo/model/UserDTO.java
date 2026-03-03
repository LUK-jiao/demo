package com.example.demo.model;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;

    private String username;

    private String email_address;

    private String token_version;
}
