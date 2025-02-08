package run.mone.m78.service.dto.knowledge;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangxiaowei6
 * @Date 2024/3/26 15:30
 */

@Data
public class KnowledgeBaseFileResDto {
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private String creator;
    private Long knowledgeBaseId;
    private String fileClass = "files";
    private String fileType;
    private String filePath;
    private String urlPath;
    private String fileName;
    private String fileContent;
    private String md5;
    private Boolean md5Change = true;
    private String fileStore = "OSS";
    private Integer embeddingStatus = 0;
    private Integer deleted = 0;
    private Long categoryId;
    private String separator;
    private boolean isSelf;
}
