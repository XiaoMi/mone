package run.mone.m78.ip.bo.z;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/11/7 13:34
 */
@Data
public class ZKnowledgeRes {

    private Long tenantId;

    private Long knowledgeBaseId;

    private Long docId;

    private Long blockId;

    private float distance;

    private String content;



}
