package run.mone.local.docean.controller.test;

import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

@Slf4j
public class MailTest {

    public static void main(String[] args) throws MessagingException, GeneralSecurityException {
        Properties props = new Properties();
        props.put("mail.host", "smtp.qq.com");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("XX@qq.com", "XX");
            }
        });
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("XX@qq.com"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("XX@qq.com"));
        message.setSubject("title");
        message.setText("content");
        Transport.send(message);
        System.out.println("##############");
    }

}
