package run.mone.moner.server.bo;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 14:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryMsg {

    private String id;

    private String role;

    private JsonObject content;

}
