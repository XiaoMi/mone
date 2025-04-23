package run.mone.mcp.ali.email.model;

import lombok.Data;

/**
 * 邮件正文实体类
 * @author hobo
 */
@Data
public class EmailBody {
    /**
     * 纯文本内容
     */
    private String bodyText;
    
    /**
     * HTML 内容
     */
    private String bodyHtml;
} 