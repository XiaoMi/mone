package run.mone.ai.gpt.bo.multiModal;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class GptVisionContent {

    @SerializedName("type")
    private String type;

    @SerializedName("text")
    private String text;

    @SerializedName("image_url")
    private Map<String, String> imageUrl;

}
