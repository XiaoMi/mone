package run.mone.ai.gpt.bo.multiModal;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/5/25 14:16
 */
@Data
@Builder
public class GptVisionMsg implements Serializable {

    @SerializedName("role")
    private String role;

    @SerializedName("content")
    private List<GptVisionContent> content;
}
