package run.mone.ai.gpt.bo.multiModal;

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
public class GptVisionRequest {

        @SerializedName("model")
        private String model;

        @SerializedName("temperature")
        private double temperature;

        @SerializedName("n")
        @Builder.Default
        private int n = 1;

        @SerializedName("stream")
        private boolean stream;

        @SerializedName("top_p")
        private double topP;

        @SerializedName("max_tokens")
        private int maxTokens;

        @SerializedName("presence_penalty")
        private double presencePenalty;

        @SerializedName("frequency_penalty")
        private double frequencyPenalty;

        @SerializedName("messages")
        private List<GptVisionMsg> messages;


}
