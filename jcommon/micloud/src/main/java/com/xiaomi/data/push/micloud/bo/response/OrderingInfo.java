package com.xiaomi.data.push.micloud.bo.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderingInfo implements Serializable {

    @SerializedName("order_state")
    private OrderState orderState;
}
