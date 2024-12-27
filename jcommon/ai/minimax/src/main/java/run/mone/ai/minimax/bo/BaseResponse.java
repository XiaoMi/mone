package run.mone.ai.minimax.bo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class BaseResponse {

    @SerializedName(value = "status_code")
    private String statusCode;

    @SerializedName(value = "status_msg")
    private String statusMsg;
}
