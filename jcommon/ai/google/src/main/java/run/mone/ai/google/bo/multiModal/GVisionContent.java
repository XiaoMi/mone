package run.mone.ai.google.bo.multiModal;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GVisionContent {

    @SerializedName("type")
    private String type;

    @SerializedName("text")
    private String text;

    @SerializedName("source")
    private GVisionSource source;
}
