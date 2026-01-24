package com.example.demo;

import com.example.demo.utils.JwtUtils;
import com.example.demo.utils.TokenRedisManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthInterceptorTest.SecureController.class)
class AuthInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private TokenRedisManager tokenRedisManager;

    @Test
    void requestWithoutTokenIsBlocked() throws Exception {
        mockMvc.perform(get("/user/register")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidTokenIsBlocked() throws Exception {
        String badToken = "bad";
        when(jwtUtils.validateToken(badToken)).thenReturn(false);

        mockMvc.perform(get("/secure")
                        .header("Authorization", "Bearer " + badToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validTokenPasses() throws Exception {
        String goodToken = "good";
        when(jwtUtils.validateToken(goodToken)).thenReturn(true);
        when(tokenRedisManager.isTokenValid(goodToken)).thenReturn(true);
        when(jwtUtils.getUserIdFromToken(goodToken)).thenReturn(1L);

        mockMvc.perform(get("/secure")
                        .header("Authorization", "Bearer " + goodToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @RestController
    @RequestMapping("/secure")
    static class SecureController {
        @GetMapping
        public String secureEndpoint() {
            return "ok";
        }
    }
}

