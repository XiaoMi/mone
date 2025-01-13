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
        FlowHttpClient client = FlowHttpClient.builder().token("e1c51eaa-2f39-4cbc-8ec0-b6ab9d117a02").build();
        String res = client.callFlow(M78FlowReq.builder()
                .flowId("127")
                .userName("wangyingjie3")
                .inputs(ImmutableMap.of("scale", "256"))
                .build(), null);
        System.out.println(res);
    }
}
