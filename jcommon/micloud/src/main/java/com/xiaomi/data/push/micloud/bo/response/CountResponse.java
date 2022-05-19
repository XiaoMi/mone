package com.xiaomi.data.push.micloud.bo.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CountResponse {

    @SerializedName("child_order")
    private long childOrder;

    @SerializedName("total_count")
    private long totalCount;

    @SerializedName("available_count")
    private long availableCount;

    @SerializedName("remaining_count")
    private long remainingCount;

    private long code;
    private String message;
    private String requestId;
}
