package run.mone.local.docean.fsm.bo;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 14:53
 */
@Data
@Builder
public class ItemKey {

    private int flowId;

    private String name;

}
