package run.mone.hive.schema;

import com.google.gson.JsonObject;
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


    public AiMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    //适应更复杂的数据
    private JsonObject jsonContent;


}
