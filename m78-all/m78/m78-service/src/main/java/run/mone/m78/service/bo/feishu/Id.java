package run.mone.m78.service.bo.feishu;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-07-30 18:27
 */
@Data
public class Id implements Serializable {
    @SerializedName("union_id")
    public String unionId;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("open_id")
    public String openId;

}
