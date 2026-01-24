package com.example.demo.request;

import lombok.Data;

@Data
public class UserReq {
    private String username;
    private String password;
    private String email;
}