package run.mone.ai.google.bo;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/4/9 16:36
 */
@Data
@Builder
public class Message {

    @SerializedName("role")
    private String role;

    @SerializedName("content")
    private String content;

}
