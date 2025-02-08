package run.mone.m78.server.ws.biz;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2024/5/18 15:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizContext {

    private JsonObject req;

    private String user;

    Long botId;

}
