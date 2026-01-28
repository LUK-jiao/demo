package com.example.demo.controller;

import com.example.demo.request.UserReq;
import com.example.demo.response.Result;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.TokenRedisManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody UserReq userReq){
        if(userReq.getUsername() == null || userReq.getPassword() == null){
            return Result.failure("Username, password must not be null");
        }
        Result result = userService.register(userReq.getUsername(), userReq.getPassword());
        log.info("Registered user: {}", userReq );
        return result;
    }

    @PostMapping("/login")
    public Result login(@RequestBody UserReq userReq){
        if(userReq.getUsername() == null || userReq.getPassword() == null){
            return Result.failure("Username, password must not be null");
        }
        log.info("Logining user: {}", userReq.getUsername());
        return userService.login(userReq.getUsername(), userReq.getPassword());
    }
}

