package run.mone.ai.google.bo.multiModal;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GVisionSource {

    @SerializedName("type")
    private String type;

    @SerializedName("media_type")
    private String mediaType;

    @SerializedName("data")
    private String data;

}
