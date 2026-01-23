package com.example.demo.controller;

import com.example.demo.request.RegisterReq;
import com.example.demo.response.Result;
import com.example.demo.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody RegisterReq registerReq){
        if(registerReq.getUsername() == null || registerReq.getPassword() == null){
            return Result.failure("Username, password must not be null");
        }
        userService.register(registerReq.getUsername(), registerReq.getPassword());
        return Result.success();
    }
}

