package run.mone.ai.google.test;

import com.google.common.collect.Lists;
import org.junit.Test;
import run.mone.ai.google.CloudeClient;
import run.mone.ai.google.bo.Message;
import run.mone.ai.google.bo.RequestPayload;
import run.mone.ai.google.bo.ResponsePayload;

/**
 * @author goodjava@qq.com
 * @date 2024/4/9 16:24
 */
public class ClientTest {

    @Test
    public void test1() {
        String content = "天空为什么是蓝色的?";
//        String content = "树上有10只鸟,我开了一枪还有几只鸟?";
        CloudeClient c = new CloudeClient();
        c.setProjectId(System.getenv("google_project_id"));
        RequestPayload payload = RequestPayload.builder().maxTokens(4000).anthropicVersion("vertex-2023-10-16").messages(Lists.newArrayList(Message.builder().role("user")
                .content(content)
                .build())).build();
        ResponsePayload r = c.call(c.token(), payload);
        System.out.println(r.getContent().get(0).getText());
    }
}
