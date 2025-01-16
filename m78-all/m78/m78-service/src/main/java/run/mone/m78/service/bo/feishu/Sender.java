package run.mone.m78.service.bo.feishu;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-07-30 18:26
 */
@Data
public class Sender implements Serializable {
    @SerializedName("sender_id")
    public SenderId senderId;
    @SerializedName("sender_type")
    public String senderType;
    @SerializedName("tenant_key")
    public String tenantKey;

}
