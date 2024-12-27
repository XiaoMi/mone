package run.mone;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

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
