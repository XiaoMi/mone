package run.mone.antlr.golang;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/1/29 16:56
 */
@Data
@Builder
public class GoMethod {

    private String name;

    private List<GoParam> paramList;

    private String code;

}
