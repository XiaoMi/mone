package run.mone.m78.api.bo.knowledge;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author wmin
 * @date 2024/1/29
 */
@Builder
@Data
public class KnowledgeConfig {

    private Long knowledgeBaseId;

    private List<Long> fileIdList;
}
