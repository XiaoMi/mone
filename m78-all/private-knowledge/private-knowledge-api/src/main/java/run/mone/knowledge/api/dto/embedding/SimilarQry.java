package run.mone.knowledge.api.dto.embedding;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author wmin
 * @date 2024/2/6
 */
@Data
@Builder
public class SimilarQry {

    private String questionContent;

    private List<SimilarQryBase> baseList;

    private Integer limit = 1;
}
