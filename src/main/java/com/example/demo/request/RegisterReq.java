package com.example.demo.request;

import lombok.Data;

@Data
public class  RegisterReq{
    private String username;
    private String password;
    private String email;
}