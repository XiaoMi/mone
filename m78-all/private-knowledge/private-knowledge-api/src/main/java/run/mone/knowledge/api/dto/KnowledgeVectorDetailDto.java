package run.mone.knowledge.api.dto;

import lombok.Data;
import run.mone.knowledge.api.enums.KnowledgeTypeEnum;


/**
 * @author wmin
 * @date 2024/2/5
 */
@Data
public class KnowledgeVectorDetailDto {

    /**
     * @see KnowledgeTypeEnum
     */
    private String type;

    private String tag1;

    private String tag2;

    private String tag3;

    private String tag4;

    private String tag5;

    private String tag6;

    private String tag7;

    private String content;

    private double[] vector;

}
