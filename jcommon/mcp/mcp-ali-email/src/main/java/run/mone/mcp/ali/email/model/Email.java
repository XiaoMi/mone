package run.mone.mcp.ali.email.model;

import lombok.Data;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * 邮件实体类
 * @author hobo
 */
@Data
public class Email {
    /**
     * 邮件ID
     */
    private String id;
    
    /**
     * 互联网消息ID
     */
    private String internetMessageId;
    
    /**
     * 邮件主题
     */
    private String subject;
    
    /**
     * 邮件摘要
     */
    private String summary;
    
    /**
     * 优先级
     */
    private String priority;
    
    /**
     * 是否需要已读回执
     */
    private Boolean isReadReceiptRequested;
    
    /**
     * 发件人信息
     */
    private EmailRecipient from;
    
    /**
     * 收件人列表
     */
    private List<EmailRecipient> toRecipients;
    
    /**
     * 抄送人列表
     */
    private List<EmailRecipient> ccRecipients;
    
    /**
     * 密送人列表
     */
    private List<EmailRecipient> bccRecipients;
    
    /**
     * 发送者信息
     */
    private EmailRecipient sender;
    
    /**
     * 回复人列表
     */
    private List<EmailRecipient> replyTo;
    
    /**
     * 邮件正文
     */
    private EmailBody body;
    
    /**
     * 邮件头信息
     */
    private Map<String, String> internetMessageHeaders;
    
    /**
     * 文件夹ID
     */
    private String folderId;
    
    /**
     * 是否包含附件
     */
    private Boolean hasAttachments;
    
    /**
     * 是否已读
     */
    private Boolean isRead;
    
    /**
     * 会话ID
     */
    private String conversationId;
    
    /**
     * 发送时间
     */
    private ZonedDateTime sentDateTime;
    
    /**
     * 最后修改时间
     */
    private ZonedDateTime lastModifiedDateTime;
    
    /**
     * 接收时间
     */
    private ZonedDateTime receivedDateTime;
    
    /**
     * 标签列表
     */
    private List<String> tags;
} 