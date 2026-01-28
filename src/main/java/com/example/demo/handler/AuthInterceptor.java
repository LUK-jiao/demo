package com.example.demo.handler;

import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.TokenRedisManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
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
            log.info("Missing or invalid Authorization header");
            return false;
        }

        String token  = authHeader.substring(7);

        if (!jwtUtils.validateToken(token) || !tokenRedisManager.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.info("invalid token:{}", token);
            return false;
        }

        Long userId = jwtUtils.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        MDC.put("userId", String.valueOf(userId));
        MDC.put("traceId", UUID.randomUUID().toString());
        log.info("Already online user:{}", userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}
