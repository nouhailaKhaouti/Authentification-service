package com.codetech.authserver.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${mail.host}")
    private String Host;
    @Value("${mail.port}")
    private String PORT;
    @Value("${mail.username}")
    private String USER_NAME;
    @Value("${mail.password}")
    private String PASSWORD;
    @Value("${mail.properties.mail.smtp.starttls.enable}")
    private String ENABLE;
    @Value("${mail.properties.mail.smtp.auth}")
    private String AUTH;
    public void sendVerificationEmail(String email, String verificationLink) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", AUTH);
        properties.put("mail.smtp.starttls.enable", ENABLE);
        properties.put("mail.smtp.host", Host);
        properties.put("mail.smtp.port", PORT);

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER_NAME, PASSWORD);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USER_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Password Reset");
            message.setText("Please click on the link below to reset your password:\n\n" + verificationLink);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
