package com.example.demo.service;

import com.example.demo.enums.ErrorCode;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.mapper.PasswordResetTokenMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.PasswordResetToken;
import com.example.demo.model.User;
import com.example.demo.model.UserVo;
import com.example.demo.response.Result;
import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.KafkaUtils;
import com.example.demo.utils.TokenRedisManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    PasswordResetTokenMapper passwordResetTokenMapper;

    @Autowired
    TokenRedisManager tokenRedisManager;

    @Autowired
    KafkaUtils kafkaUtils;

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
        tokenRedisManager.storeToken(token, user.getId(),30L);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        userVo.setToken(token);
        return Result.success(userVo);
    }

    public User getUserByUsername(String userName){
        User user = userMapper.selectByUsername(userName);
        if(user == null){
            return null;
        }
        return user;
    }


    public boolean forgetPW(String username) {
        User user = getUserByUsername(username);
        if(user == null){
            return false;
        }//首先生成token，存库，通过邮箱发送链接
        String tokenHash = jwtUtils.generateToken(user.getId());
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUserId(user.getId());
        passwordResetToken.setTokenHash(tokenHash);
        try{
            passwordResetTokenMapper.insert(passwordResetToken);//todo 需要事务
            kafkaUtils.send(passwordResetToken);
        }catch (Exception e){
            log.info("UserService forgetPW error :{}",e.getMessage());
            return false;
        }
        return true;
    }
}
