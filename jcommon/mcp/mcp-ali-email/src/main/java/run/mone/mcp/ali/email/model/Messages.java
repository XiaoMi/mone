package run.mone.mcp.ali.email.model;

import lombok.Data;
import java.util.List;

/**
 * 邮件列表响应实体类
 * @author hobo
 */
@Data
public class Messages {
    /**
     * 邮件列表
     */
    private List<Email> messages;
    
    /**
     * 下一页游标
     */
    private String nextCursor;
    
    /**
     * 是否还有更多数据
     */
    private Boolean hasMore;
} 