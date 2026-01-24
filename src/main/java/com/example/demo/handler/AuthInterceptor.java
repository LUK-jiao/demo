package com.example.demo.handler;

import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.TokenRedisManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    TokenRedisManager tokenRedisManager;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token  = authHeader.substring(7);

        if (!jwtUtils.validateToken(token) || !tokenRedisManager.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        Long userId = jwtUtils.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        return true;
    }

}
