package run.mone.m78.service.bo.feishu;

import lombok.Data;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-07-29 23:47
 */


@Data
public class Header implements Serializable {

    @SerializedName("event_id")
    public String eventId;
    @SerializedName("event_type")
    public String eventType;
    @SerializedName("create_time")
    public String createTime;
    @SerializedName("token")
    public String token;
    @SerializedName("app_id")
    public String appId;
    @SerializedName("tenant_key")
    public String tenantKey;
}

