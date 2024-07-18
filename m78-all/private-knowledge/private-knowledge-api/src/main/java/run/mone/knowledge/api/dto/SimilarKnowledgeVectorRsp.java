package run.mone.knowledge.api.dto;

import lombok.Builder;
import lombok.Data;


/**
 * @author wmin
 * @date 2024/2/5
 */
@Data
@Builder
public class SimilarKnowledgeVectorRsp {

    private Double similarity;

    private String group;

    private String leaf;

}
