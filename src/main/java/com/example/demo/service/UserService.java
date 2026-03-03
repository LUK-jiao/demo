package com.example.demo.service;

import com.example.demo.enums.ErrorCode;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.mapper.PasswordResetTokenMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.*;
import com.example.demo.response.Result;
import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.KafkaUtils;
import com.example.demo.utils.TokenRedisManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public UserDTO verifyUserEmail(String username){
        User user = userMapper.selectByUsername(username);
        UserDTO userDTO = new UserDTO();
        if(user == null){
            return null;
        }
        BeanUtils.copyProperties(user,userDTO);
        return userDTO;
    }

    public boolean forgetPW(String emailAddress,Long userId) {
        //生成token，存库，通过邮箱发送链接
        String tokenHash = jwtUtils.generateToken(userId);
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUserId(userId);
        passwordResetToken.setTokenHash(tokenHash);
        try{
            passwordResetTokenMapper.insert(passwordResetToken);//todo 需要事务
        }catch (Exception e){
            log.info("UserService forgetPW error :{}",e.getMessage());
            return false;
        }
        //发消息
        MailMessage  mailMessage = new MailMessage();
        mailMessage.setEamil_address(emailAddress);
        mailMessage.setPasswordResetToken(passwordResetToken);
        kafkaUtils.send(mailMessage);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(User updateUser) {
        try {
            int result = userMapper.updateById(updateUser);
            if (result == 0) {
                throw new BusinessException("用户不存在或更新失败");
            }
        } catch (Exception e) {
            log.error("UserService resetPassword error :", e);  // 使用 error 级别，打印堆栈
            throw new BusinessException("密码重置失败: " + e.getMessage());  // 重新抛出
        }
    }
}
