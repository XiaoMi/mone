package run.mone.antlr.golang;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/1/29 17:02
 */
@Data
@Builder
public class GoParam {

    private String name;

    private String type;

}
