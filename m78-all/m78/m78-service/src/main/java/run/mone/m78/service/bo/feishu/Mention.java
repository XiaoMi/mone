package run.mone.m78.service.bo.feishu;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-07-29 23:47
 */
@Data
public class Mention implements Serializable {
    @SerializedName("key")
    public String key;
    @SerializedName("id")
    public Id id;
    @SerializedName("name")
    public String name;
    @SerializedName("tenant_key")
    public String tenantKey;
}