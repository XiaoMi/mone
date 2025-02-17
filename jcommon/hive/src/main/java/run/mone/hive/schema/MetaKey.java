package run.mone.hive.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2025/1/6 16:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaKey {

    private String key;

    private String desc;

}
