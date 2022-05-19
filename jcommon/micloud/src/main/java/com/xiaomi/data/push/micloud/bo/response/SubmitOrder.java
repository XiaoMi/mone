package com.xiaomi.data.push.micloud.bo.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SubmitOrder implements Serializable {

    private String msg;

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("suborder_ids")
    private List<Integer> suborderIds;

}
