package run.mone.m78.api.bo.knowledge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-07 17:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KnowledgeBo implements Serializable {

    private Long knowledgeBaseId;
    private String knowledgeName;
    private Date gmtCreate;
    private Date gmtModified;
    private String creator;
    private String remark;
    private String avatarUrl;
    private Integer version;
}
