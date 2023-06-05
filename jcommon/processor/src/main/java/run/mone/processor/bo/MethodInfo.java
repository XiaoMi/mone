package run.mone.processor.bo;

import lombok.Builder;
import run.mone.processor.anno.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/5 10:21
 */
@Data
@Builder
public class MethodInfo implements Serializable {

    private String name;

    private String code;

}
