package run.mone.ai.google.bo;

import lombok.Data;

import com.google.gson.annotations.SerializedName;

/**
 * @author goodjava@qq.com
 * @date 2024/4/9 16:43
 */
@Data
public class Content {

    @SerializedName("type")
    private String type;

    @SerializedName("text")
    private String text;

}
