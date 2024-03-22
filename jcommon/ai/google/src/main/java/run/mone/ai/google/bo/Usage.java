package run.mone.ai.google.bo;

import com.google.gson.annotations.SerializedName;

/**
 * @author goodjava@qq.com
 * @date 2024/4/9 16:42
 */
public class Usage {


    @SerializedName("input_tokens")
    private int inputTokens;

    @SerializedName("output_tokens")
    private int outputTokens;


}
