package run.mone.aws.client.test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import run.mone.AwsClient;
import run.mone.Key;
import run.mone.ModelEnum;
import run.mone.ResponsePayload;
import software.amazon.awssdk.regions.Region;

/**
 * @author goodjava@qq.com
 * @date 2024/4/12 14:57
 */
public class AwsClientTest {


    @Test
    public void test1() {
        JSONObject payload = new JSONObject()
                .put("anthropic_version", "bedrock-2023-05-31")
                .put("max_tokens", 1000)
                .put("messages", new JSONArray()
                        .put(new JSONObject().put("role", "user")
                                .put("content", "天空为什么是蓝色的?"
                                )
                        )
                );
        ResponsePayload res = AwsClient.call(payload, Region.EU_WEST_3, ModelEnum.Haiku.modelName, Key.builder().keyId("").key("").build());
        System.out.println(res.getContent().get(0).getText());
    }

}
