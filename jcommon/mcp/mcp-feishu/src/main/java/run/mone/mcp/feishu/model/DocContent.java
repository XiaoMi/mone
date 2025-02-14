package run.mone.mcp.feishu.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DocContent {
    private String documentId;
    private String title;
    private String folderToken;
    private List<DocBlock> blocks;
    private Long createTime;
    private Long updateTime;
    private String creator;
    private String owner;
    private String url;  // 文档访问链接
} 