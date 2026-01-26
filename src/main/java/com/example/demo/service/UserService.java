package com.example.demo.service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.response.Result;
import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.TokenRedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    TokenRedisManager tokenRedisManager;

    public Result register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        if(userMapper.selectByUsername(username) != null){
            return Result.failure("Username already exists");
        }
        try{
            userMapper.insert(user);
            return Result.success();
        }
        catch (DuplicateKeyException e){
            return Result.failure("Registration failed");  //in case of TOCTOU
        }

    }

    public Result login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return Result.failure("User does not exist");
        }
        if(!user.getPassword().equals(password)){
            return Result.failure("Incorrect password");
        }
        String token = jwtUtils.generateToken(user.getId());
        tokenRedisManager.storeToken(token, user.getId());
        return Result.success(token);
    }

    public User getUserByUsername(Long userName){
        User user = userMapper.selectByUsername(String.valueOf(userName));
        if(user == null){
            return null;
        }
        return user;
    }
}
