package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.mapper.PasswordResetTokenMapper;
import com.example.demo.model.PasswordResetToken;
import com.example.demo.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenMapper passwordResetTokenMapper;

    @Autowired
    JwtUtils jwtUtils;

    public Long verifyTokenHash(String token) {
        Long userId = jwtUtils.getUserIdFromToken(token);
        QueryWrapper<PasswordResetToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token_hash", token);
        queryWrapper.eq("user_id", userId);
        PasswordResetToken passwordResetToken = passwordResetTokenMapper.selectOne(queryWrapper);
        if(passwordResetToken == null){
            log.info("tokenHash不存在");
            return null;
        }
        if(passwordResetToken.getTokenHash().equals(token)){
            Date expireAt = passwordResetToken.getExpiresAt();
            Date now = new Date();
            if(now.after(expireAt)){
                log.info("tokenHash 已过期");
                return null;
            }
        }
        return userId;
    }
}
