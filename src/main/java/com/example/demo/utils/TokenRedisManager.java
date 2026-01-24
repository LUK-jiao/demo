package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TokenRedisManager {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void storeToken(String token,Long userId) {
        stringRedisTemplate.opsForValue().set(token, String.valueOf(userId),30L, TimeUnit.MINUTES);
    }

    public boolean isTokenValid(String token) {
        return stringRedisTemplate.hasKey(token);
    }

    public void removeToken(String token) {
        stringRedisTemplate.delete(token);
    }
}
