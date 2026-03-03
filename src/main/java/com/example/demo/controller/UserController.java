package com.example.demo.controller;

import com.example.demo.enums.ErrorCode;
import com.example.demo.model.User;
import com.example.demo.model.UserDTO;
import com.example.demo.request.UserReq;
import com.example.demo.response.Result;
import com.example.demo.service.PasswordResetTokenService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordResetTokenService passwordResetTokenService;

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

    @PostMapping("/forgetPW")
    public Result forgetPW(@RequestBody UserReq userReq){
        log.info("forgetPW userReq: {}", userReq);
        if(userReq.getUsername() == null){
            return Result.failure(ErrorCode.PARAM_INVALID.getMessage());
        }
        UserDTO userDTO = userService.verifyUserEmail(userReq.getUsername());
        if(userDTO == null){
            return Result.failure("User does not exist,please check");
        }
        if(userDTO.getEmail_address() == null){
            return Result.failure("Email address is null,please 先绑定email");
        }
        if(userService.forgetPW(userReq.getUsername(),userDTO.getId())){
            return Result.failure(ErrorCode.OTHER.getMessage());
        }
        return Result.successWithMsg("A reset email has been sent, please check your email");
    }

    @PostMapping("/resetPW")
    public Result resetPW(@RequestBody UserReq userReq, @RequestHeader("tokenhash") String tokenHash){
        log.info("resetPW userReq: {},tokenHash = {}", userReq, tokenHash);
        if(tokenHash == null){
            log.info("tokenHash is null");
            return Result.failure(ErrorCode.TOKEN_IS_NULL.getMessage());
        }
        //后面就是调service，首先校验token是否正确
        Long userId = passwordResetTokenService.verifyTokenHash(tokenHash);
        if(userId == null){
            return Result.failure(ErrorCode.TOKEN_INVALID.getMessage());
        }
        //修改密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(userReq.getPassword());
        userService.resetPassword(updateUser);
        return Result.successWithMsg("Reset Password Successfully");
    }
}

