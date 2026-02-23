package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TokenRedisManager {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     *
     * @param token
     * @param userId
     * @param expireTime(minutes)
     */
    public void storeToken(String token,Long userId,long expireTime) {
        stringRedisTemplate.opsForValue().set(token, String.valueOf(userId),expireTime, TimeUnit.MINUTES);
    }

    public boolean isTokenValid(String token) {
        return stringRedisTemplate.hasKey(token);
    }

    public void removeToken(String token) {
        stringRedisTemplate.delete(token);
    }
}
