package run.mone.knowledge.api.dto;

import lombok.Data;
import run.mone.knowledge.api.enums.KnowledgeTypeEnum;

import java.util.List;


/**
 * @author wmin
 * @date 2024/2/5
 */
@Data
public class KnowledgeVectorDto {

    /**
     * @see KnowledgeTypeEnum
     */
    private String type;

    /**
     * todo 是否需要向量化处理，true则DetailDto中content必传，false则vector必传
     */
    private boolean needEmbedding;

    private List<KnowledgeVectorDetailDto> knowledgeVectorDetailDtoList;

    /**
     * 是否强制删除
     * 默认false，软删，对于软删的meta，不再insert or update其关联的detail
     * 编程助手upload code场景，需要强制删除，因为后续同一个group id可能需要更新
     */
    private boolean forceDelete;

}
