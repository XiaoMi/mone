package com.xiaomi.data.push.micloud.bo.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderState implements Serializable {

    @SerializedName("display_name")
    private String displayName;

    private int id;
}
