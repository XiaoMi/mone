package run.mone.mcp.idea.composer.handler.biz;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.FluxSink;
import run.mone.mcp.idea.composer.handler.BotClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HawickMason@xiaomi.com
 * @date 11/27/24 12:01
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BotChainCallContext {

    private String prompt;

    private BotClient botClient;

    private Map<String, Object> params;



    public static BotChainCallContext of(String prompt, FluxSink<String> fluxSink) {
        return BotChainCallContext.builder()
                .botClient(new BotClient(fluxSink))
                .prompt(prompt)
                .params(new HashMap<>())
                .build();
    }

    public void addParam(String key, Object value) {
        Preconditions.checkArgument(params != null, "required params should NEVER be NULL!");
        this.params.put(key, value);
    }


    public boolean bugfix() {
        Map<String, Object> params = this.getParams();
        return params.containsKey(Const.BOT_CHAIN_TYPE)
                && ((String) params.get(Const.BOT_CHAIN_TYPE)).equals(Const.FIX_BUG_BOT_CHAIN);
    }
}
