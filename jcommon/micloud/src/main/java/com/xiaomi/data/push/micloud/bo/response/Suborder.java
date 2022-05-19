package com.xiaomi.data.push.micloud.bo.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Suborder implements Serializable {

    @SerializedName("delivery_info")
    private List<DeliveryInfo> deliveryInfo;

}
