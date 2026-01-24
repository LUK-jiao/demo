package com.example.demo.service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

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
        return Result.success();

    }

}
