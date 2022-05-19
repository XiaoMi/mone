package com.xiaomi.data.push.micloud.bo.request;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CountRequest {
    @SerializedName("child_order_id")
    private long childOrderId;

    @SerializedName("client_token")
    private String clientToken;
}
