package run.mone.mcp.email.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@Data
@Slf4j
public class EmailFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "email_executor";
    private String desc = "Send mail";
    private ObjectMapper objectMapper;

    private String emailToolSchema = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["send_mail"],
                        "description": "Email operation to execute"
                    },
                    "mailto": {
                        "type": "string",
                        "description": "The mailto of the mail"
                    },
                    "subject": {
                        "type": "string",
                        "description": "The subject of the mail"
                    },
                    "body": {
                        "type": "string",
                        "description": "The body of the mail"
                    }
                },
                "required": ["operation", "mailto", "subject", "body"]
            }
            """;

    public EmailFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> params) {
        String host = System.getenv().getOrDefault("SMTP_HOST", "");
        String port = System.getenv().getOrDefault("SMTP_PORT", "");
        String from = System.getenv().getOrDefault("SMTP_USERNAME", "");
        String password = System.getenv().getOrDefault("SMTP_PASSWORD", "");

        if (host.isEmpty() || port.isEmpty() || from.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("SMTP_HOST, SMTP_PORT, SMTP_USERNAME and SMTP_PASSWORD must be set");
        }

        String operation = (String) params.get("operation");
        String mailto = (String) params.get("mailto");
        String subject = (String) params.get("subject");
        String body = (String) params.get("body");

        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation is required");
        }

        if (mailto == null || mailto.trim().isEmpty()) {
            throw new IllegalArgumentException("Mailto is required");
        }

        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject is required");
        }

        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Body is required");
        }

        try {
            // 设置邮件会话属性
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            return sendEmail(props, from, password, mailto, subject, body);
        } catch (Exception e) {
            log.error("Failed to send mail", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }

    private McpSchema.CallToolResult sendEmail(Properties props, String from, String password, String to, String subject, String body) {

        // 创建Session实例对象
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

        try {
            // 创建默认的MimeMessage对象
            Message message = new MimeMessage(session);

            // 设置From: 头部头字段
            message.setFrom(new InternetAddress(from));

            // 设置To: 头部头字段
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            message.setSubject(subject);

            message.setText(body);

            Transport.send(message);

            log.info("Send mail successfully");
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Send mail successfully")),
                    false
            );

        } catch (MessagingException e) {
            log.error("Failed to send mail", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }
} 