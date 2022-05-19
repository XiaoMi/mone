package com.xiaomi.data.push.micloud.bo.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Detail {
    private long id;
    private long created;
    @SerializedName("total_order_id")
    private long totalOrderId;

    @SerializedName("child_order_id")
    private long childOrderId;
    private String hostname;
    String ip;
    String sn;
    String state;
    boolean available;

}
