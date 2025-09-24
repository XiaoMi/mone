package run.mone.hive.schema;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2024/12/29 14:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Expr {

    private String key;

    private boolean input;

    private String expr;

    private String desc;

    //被提取出来的
    private JsonElement value;

}
