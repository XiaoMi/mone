package com.xiaomi.data.push.micloud.bo.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class BookResponse {
    @SerializedName("child_order_id")
    private String childOrderId;

    @SerializedName("fail_errs")
    private List<String> failErrors;
}
