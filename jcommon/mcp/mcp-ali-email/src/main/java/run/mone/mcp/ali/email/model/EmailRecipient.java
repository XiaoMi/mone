package run.mone.mcp.ali.email.model;

import lombok.Data;

/**
 * 邮件收件人实体类
 * @author hobo
 */
@Data
public class EmailRecipient {
    /**
     * 邮箱地址
     */
    private String email;
    
    /**
     * 收件人名称
     */
    private String name;
} 