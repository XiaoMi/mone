package run.mone.m78.service.bo.knowledge;

import lombok.Data;

/**
 * @author zhangxiaowei6
 * @Date 2024/3/18 17:06
 */

@Data
public class KnowledgeCreateV2Req {
    private Long id;
    private String name;
    private String remark;
    private Integer status;
    private Integer auth;
    private String type;
    private String labels;
    private String avatarUrl;
    private Long workSpaceId;
}
