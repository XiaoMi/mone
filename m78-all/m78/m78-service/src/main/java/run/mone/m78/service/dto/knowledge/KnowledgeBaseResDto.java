package run.mone.m78.service.dto.knowledge;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhangxiaowei6
 * @Date 2024/3/26 15:24
 */

@Data
public class KnowledgeBaseResDto {
    private Long id;

    private Long knowledgeBaseId;

    private String type;

    private int status;

    private int auth;

    private String knowledgeBaseName;

    private String labels;

    private String remark;

    private String avatarUrl;

    private String creator;

    private String updater;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private boolean isSelf;

    private Long workSpaceId;

}
