package run.mone;

import com.google.gson.Gson;
import org.json.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.ResolveIdentityRequest;
import software.amazon.awssdk.identity.spi.internal.DefaultAwsCredentialsIdentity;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * @date 2024/4/12 13:35
 */
public class AwsClient {

    private static final Gson gson = new Gson();


    public static ResponsePayload call(JSONObject payload, Region region, String modelId, Key key) {
        BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .credentialsProvider(new IdentityProvider<>() {
                    @Override
                    public Class<AwsCredentialsIdentity> identityType() {
                        return AwsCredentialsIdentity.class;
                    }

                    public CompletableFuture<AwsCredentialsIdentity> resolveIdentity(ResolveIdentityRequest request) {
                        return CompletableFuture.completedFuture(DefaultAwsCredentialsIdentity.builder().accessKeyId(key.getKeyId()).secretAccessKey(key.getKey()).build());
                    }
                })
                .region(region)
                .build();

        InvokeModelRequest request = InvokeModelRequest.builder()
                .contentType("application/json")
                .body(SdkBytes.fromUtf8String(payload.toString()))
                .modelId(modelId)
                .build();

        InvokeModelResponse resp = client.invokeModel(request);
        String str = new String(resp.body().asByteArray());
        return gson.fromJson(str, ResponsePayload.class);
    }
}