package run.mone.local.docean.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/2/24 09:58
 */
@Data
@Builder
public class AgentInfo implements Serializable {

    private String name;

    private Long knowledgeId;

    private Long promptId;

}
