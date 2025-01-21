package run.mone.local.docean.po.m78;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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

    private Integer version;
}
