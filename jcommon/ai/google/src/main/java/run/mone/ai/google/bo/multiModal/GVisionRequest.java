package run.mone.ai.google.bo.multiModal;

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
public class GVisionRequest {


        @SerializedName("anthropic_version")
        private String anthropicVersion;

        @SerializedName("messages")
        private List<GVisionMsg> messages;

        @SerializedName("max_tokens")
        private int maxTokens;

        @SerializedName("stream")
        private boolean stream;



}
