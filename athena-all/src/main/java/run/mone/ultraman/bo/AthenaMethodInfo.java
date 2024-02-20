package run.mone.ultraman.bo;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/11/6 22:43
 */
@Data
@Builder
public class AthenaMethodInfo {

    private String name;

    private String code;

    private String md5;

    //相关性评分
    @Builder.Default
    private int score = 0;

}
