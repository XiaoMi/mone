package run.mone.local.docean.po;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/2/26 18:14
 */
@Data
@Builder
public class FunctionInfo implements Serializable {

    private String script;

    private String functionName;

}
