package com.example.demo;

import com.example.demo.model.User;
import com.example.demo.response.Result;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class UserRegisterConcurrencyTest {
    @Autowired
    UserService userService;

    @Test
    void testConcurrentRegister() throws InterruptedException {
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                User user = new User();
                user.setUsername("concurrentUser");
                user.setPassword("password123");
                try {
                    Result res = userService.register(user.getUsername(), user.getPassword());
                    System.out.println(Thread.currentThread().getName() + ": " + res.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();
    }
}
