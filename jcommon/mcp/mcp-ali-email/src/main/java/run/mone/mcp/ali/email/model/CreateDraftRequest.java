package run.mone.mcp.ali.email.model;

import lombok.Data;
import java.util.List;

/**
 * 创建草稿请求实体类
 * @author hobo
 */
@Data
public class CreateDraftRequest {
    /**
     * 邮件主题
     */
    private String subject;
    
    /**
     * 收件人列表
     */
    private List<EmailRecipient> toRecipients;
    
    /**
     * 邮件正文
     */
    private EmailBody body;
} 