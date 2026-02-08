package com.example.demo.model;

import lombok.Data;

@Data
public class UserVo {

    private Long id;

    private String username;

    private String password;

    private String token;
}
