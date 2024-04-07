package run.mone.local.docean.fsm;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/2/27 16:28
 */
@Data
@Builder
public class MemoryData {

    private String role;

    private String message;

}
