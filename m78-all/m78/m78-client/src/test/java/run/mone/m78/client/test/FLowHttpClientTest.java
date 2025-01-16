package run.mone.m78.client.test;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import run.mone.m78.client.bot.BotHttpClient;
import run.mone.m78.client.flow.FlowHttpClient;
import run.mone.m78.client.model.M78BotReq;
import run.mone.m78.client.model.M78FlowReq;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/26/24 15:04
 */
public class FLowHttpClientTest {
    @Test
    public void testBotHttpClient() {
        FlowHttpClient client = FlowHttpClient.builder().token("token").build();
        String res = client.callFlow(M78FlowReq.builder()
                .flowId("1")
                .userName("name")
                .inputs(ImmutableMap.of("scale", "256"))
                .build(), null);
        System.out.println(res);
    }
}
