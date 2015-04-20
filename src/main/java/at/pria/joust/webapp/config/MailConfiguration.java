package at.pria.joust.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfiguration {
    @Bean
    public MailSender mailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("REDACTED");
        sender.setProtocol("smtps");
        sender.setUsername("REDACTED");
        sender.setPassword("REDACTED");
        return sender;
    }

    @Bean
    public SimpleMailMessage registerMailTemplate() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("REDACTED");
        msg.setSubject("Test");
        msg.setText("Test Mail");
        return msg;
    }
}
