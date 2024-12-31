package run.mone.knowledge.api.dto.embedding;

import lombok.Builder;
import lombok.Data;

/**
 * @author wmin
 * @date 2024/2/6
 */
@Data
@Builder
public class SimilarQryBase {

    private Long id;

    private byte[] embedding;

}
