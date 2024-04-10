package run.mone.ai.google.bo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/4/9 16:41
 */
@Data
public class ResponsePayload {

    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private String type;

    @SerializedName("role")
    private String role;

    @SerializedName("content")
    private List<Content> content;

    @SerializedName("model")
    private String model;

    @SerializedName("stop_reason")
    private String stopReason;

    @SerializedName("stop_sequence")
    private Object stopSequence; // Use Object if the value can be null or of different types

    @SerializedName("usage")
    private Usage usage;

}
