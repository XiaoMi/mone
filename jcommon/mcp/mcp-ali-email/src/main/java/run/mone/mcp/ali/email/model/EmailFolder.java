package run.mone.mcp.ali.email.model;

import lombok.Data;
import java.util.Map;

/**
 * 邮件文件夹实体类
 * @author hobo
 */
@Data
public class EmailFolder {
    /**
     * 文件夹ID
     */
    private String id;
    
    /**
     * 显示名称
     */
    private String displayName;
    
    /**
     * 父文件夹ID
     */
    private String parentFolderId;
    
    /**
     * 子文件夹数量
     */
    private Integer childFolderCount;
    
    /**
     * 总项目数量
     */
    private Integer totalItemCount;
    
    /**
     * 未读项目数量
     */
    private Integer unreadItemCount;
    
    /**
     * 扩展属性
     */
    private Map<String, String> extensions;
} 