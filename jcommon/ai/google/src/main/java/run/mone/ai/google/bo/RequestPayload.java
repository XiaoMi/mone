package run.mone.ai.google.bo;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/4/9 16:36
 */
@Data
@Builder
public class RequestPayload {

        @SerializedName("system")
        private String system;

        @SerializedName("anthropic_version")
        private String anthropicVersion;

        @SerializedName("messages")
        private List<Message> messages;

        @SerializedName("max_tokens")
        private int maxTokens;

        @SerializedName("stream")
        private boolean stream;



}
