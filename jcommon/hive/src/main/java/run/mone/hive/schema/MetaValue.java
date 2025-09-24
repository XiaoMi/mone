package run.mone.hive.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2025/1/6 16:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaValue {

    private Object value;

    private String desc;


}
