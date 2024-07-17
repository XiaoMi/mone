package run.mone.knowledge.api.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wmin
 * @date 2024/2/5
 */
@Data
public class SimilarKnowledgeVectorQry {

    private String type;

    private List<TagsInfo> tagsInfoList;

    private String questionContent;

    private double[] questionVector;

    private Integer topN;

    private Double similarity;

}
