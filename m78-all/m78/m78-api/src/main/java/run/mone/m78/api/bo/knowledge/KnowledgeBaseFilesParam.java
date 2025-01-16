package run.mone.m78.api.bo.knowledge;

import lombok.Data;

/**
 * @author wmin
 * @date 2024/1/29
 */
@Data
public class KnowledgeBaseFilesParam {

    private Long knowledgeBaseId;

    private String filePath;

    private String fileName;

    private Long fileId;

    private String separator;

    private String urlPath;
}
