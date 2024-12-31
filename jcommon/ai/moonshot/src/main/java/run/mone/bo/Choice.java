package run.mone.bo;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/3/26 16:12
 */
@Data
@Builder
public class Choice {


    private int index;

    private Message message;

    @SerializedName("finish_reason")
    private String finishReason;

}
