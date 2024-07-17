package run.mone.knowledge.api.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wmin
 * @date 2024/2/19
 */
@Data
public class KnowledgeVectorDetailFullDto {

    private KnowledgeVectorDetailDto detailDto;

    private String type;

    //完整的tags，七层
    private List<String> fullTags;

    private String groupTag;

    private String leafTag;

    private int groupTagIndex;

    private int leafTagIndex;

}
