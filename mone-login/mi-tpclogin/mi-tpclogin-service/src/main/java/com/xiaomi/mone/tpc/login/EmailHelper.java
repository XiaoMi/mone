package com.xiaomi.mone.tpc.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class EmailHelper {

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String mailUserName;

    /**
     * 密码重置
     * @param email
     * @param findUrl
     */
    public boolean sendResetPwd(String email, String findUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("TPC账号密码重置");
            message.setFrom(mailUserName);
            message.setTo(email);
            message.setSentDate(new Date());
            message.setText("请在5分钟内完成密码设置（勿告诉他人）；" + findUrl);
            javaMailSender.send(message);
            log.info("邮件发送成功email={}", email);
            return true;
        } catch (Throwable e) {
            log.error("邮件发送失败email={}", email, e);
            return false;
        }
    }
}
