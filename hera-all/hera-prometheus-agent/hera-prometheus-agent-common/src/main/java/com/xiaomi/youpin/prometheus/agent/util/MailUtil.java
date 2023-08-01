package com.xiaomi.youpin.prometheus.agent.util;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Properties;
/**
 * @author zhangxiaowei6
 */
@Slf4j
@Component
public class MailUtil {

    @NacosValue(value = "${mail.user.name}", autoRefreshed = true)
    private String mail_user_name;

    @NacosValue(value = "${mail.pwd.code}", autoRefreshed = true)
    private String mail_pwd_code;

    @NacosValue(value = "${mail.host}", autoRefreshed = true)
    private String mail_host;

    @NacosValue(value = "${mail.smtp.auth}", autoRefreshed = true)
    private String mail_smtp_auth;

    @NacosValue(value = "${mail.smtp.port}", autoRefreshed = true)
    private String mail_smtp_port;


    private Session myMailSession;

    //Establish a mail connection to the server so that you can send mail
    private Session getMailSession() {
        if (null != myMailSession) {
            return myMailSession;
        }
        Properties properties = new Properties();
        //Get the address of the email smtp server
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.host", mail_host);
        //Whether to perform permission verification.
        properties.setProperty("mail.smtp.auth", mail_smtp_auth);
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.port", mail_smtp_port);
        //properties.setProperty("mail.smtp.socketFactory.class", sslFactory);
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.socketFactory.port", mail_smtp_port);
        properties.put("mail.smtp.starttls.enable", "false");
        properties.put("mail.smtp.starttls.required", mail_smtp_auth);
        if (mail_smtp_auth.equals("true")) {
            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        }
        log.info("mail property: {}",properties.toString());
        //Determine permissions (account and password)
        Authenticator authenticator = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                //Fill in the login account and authorization password of your own mailbox (not the login password)
                return new PasswordAuthentication(mail_user_name, mail_pwd_code);
            }
        };
        myMailSession = Session.getDefaultInstance(properties, authenticator);
        return myMailSession;
    }

    public void sendMailTOSingleUser(String toEmailAddress, String title, String content) {
        log.info("sendMailTOSingleUser: toEmailAddress:{}, title:{}, content:{}", toEmailAddress, title, content);
        //Get a connection to the email server
        Session session = getMailSession();
        Message message = new MimeMessage(session);
        // Sender Our own email address, which is the name
        try {
            message.setFrom(new InternetAddress(mail_user_name));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toEmailAddress));
            message.setRecipient(Message.RecipientType.CC, new InternetAddress(mail_user_name));
            message.setSubject(title);
            //Set the encoding to prevent Chinese garbled characters in the sent content.
            message.setContent(content, "text/html;charset=UTF-8");
            //Send a message
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendMailToUserArray(ArrayList<String> toEmailAddressArrayList, String title, String content) {
        log.info("sendMailToUserArray: toEmailAddressArrayList:{}, title:{}, content:{}", toEmailAddressArrayList, title, content);
        String[] toEmailAddressArray = (String[]) toEmailAddressArrayList.toArray(new String[0]);
        //Get a connection to the email server
        Session session = getMailSession();
        Message message = new MimeMessage(session);
        // Sender Our own email address is the name
        try {
            message.setFrom(new InternetAddress(mail_user_name));

            if (toEmailAddressArray != null) {
                // Create an address for each mail recipient
                Address[] toEmailRecipients = null;
                toEmailRecipients = new InternetAddress[toEmailAddressArray.length];
                for (int i = 0; i < toEmailAddressArray.length; i++) {
                    toEmailRecipients[i] = new InternetAddress(toEmailAddressArray[i]);
                }
                message.setRecipients(MimeMessage.RecipientType.TO, toEmailRecipients);

            }
            //Set the CC, here you set your own as the CC, so as not to be listed as spam by mailbox
            message.setRecipient(Message.RecipientType.CC, new InternetAddress(mail_user_name));
            message.setSubject(title);
            //Set the encoding to prevent Chinese garbled characters in the sent content.
            message.setContent(content, "text/html;charset=UTF-8");
            //Send a message
            Transport.send(message);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }

}
