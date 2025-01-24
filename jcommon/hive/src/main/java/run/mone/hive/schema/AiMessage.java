package run.mone.hive.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2025/1/2 09:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiMessage {

    private String role;

    private String content;


}
