package com.example.demo;

import com.example.demo.service.MailService;
import com.example.demo.model.MailMessage;
import com.example.demo.model.PasswordResetToken;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import jakarta.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(initializers = MailServiceTest.MailTestProperties.class)
class MailServiceTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP).withPerMethodLifecycle(true);

    @Autowired
    private MailService mailService;

    @Test
    void sendResetMail_sendsMessage() throws Exception {
        // Arrange: prepare a mail message
        PasswordResetToken token = new PasswordResetToken();
        token.setTokenHash("dummy-token-hash");

        MailMessage mailMessage = new MailMessage();
        mailMessage.setEmailAddress("test@example.com");
        mailMessage.setPasswordResetToken(token);

        // Act
        mailService.sendResetMail(mailMessage);

        // Assert: message was received by GreenMail
        greenMail.waitForIncomingEmail(1);
        MimeMessage[] received = greenMail.getReceivedMessages();
        assertThat(received).hasSize(1);
        assertThat(received[0].getAllRecipients()[0].toString()).isEqualTo("test@example.com");
        assertThat(received[0].getSubject()).contains("忘记密码");
        assertThat(received[0].getContent().toString()).contains("dummy-token-hash");
    }

    /**
     * Override mail properties so MailUtils sends through the in-memory GreenMail server instead of real SMTP.
     */
    static class MailTestProperties implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "spring.mail.host=" + ServerSetupTest.SMTP.getBindAddress(),
                    "spring.mail.port=" + ServerSetupTest.SMTP.getPort(),
                    "spring.mail.username=",  // 清空用户名
                    "spring.mail.password=",  // 清空密码
                    "spring.mail.properties.mail.smtp.auth=false",
                    "spring.mail.properties.mail.smtp.starttls.enable=false",
                    "spring.mail.properties.mail.smtp.ssl.enable=false",
                    "spring.mail.test-connection=false",  // 禁用测试连接
                    // 如果 MailUtils 使用了自定义的 163mail 配置，也需要覆盖
                    "163mail.username=",
                    "163mail.password="
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}

