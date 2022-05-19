package com.xiaomi.data.push.micloud.bo.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderDetail implements Serializable {

    @SerializedName("orderng_info")
    private OrderingInfo orderingInfo;

    @SerializedName("suborders")
    private List<Suborder> suborders;
}
