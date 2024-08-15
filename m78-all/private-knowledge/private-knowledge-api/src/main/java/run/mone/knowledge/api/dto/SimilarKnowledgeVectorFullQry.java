package run.mone.knowledge.api.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wmin
 * @date 2024/2/19
 */
@Data
public class SimilarKnowledgeVectorFullQry {

    private SimilarKnowledgeVectorQry vectorQry;

    private List<String> tags;

    private String groupTag;

    private String leafTag;
}
