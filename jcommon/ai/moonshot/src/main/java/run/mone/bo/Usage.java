package run.mone.bo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2024/3/26 16:13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usage {


    @SerializedName("prompt_tokens")
    private int promptTokens;
    @SerializedName("completion_tokens")
    private int completionTokens;
    @SerializedName("total_tokens")
    private int totalTokens;


}
