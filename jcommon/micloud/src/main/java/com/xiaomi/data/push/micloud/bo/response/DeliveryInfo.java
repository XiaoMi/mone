package com.xiaomi.data.push.micloud.bo.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeliveryInfo implements Serializable {

    private String hostname;
    private String ipaddr;
}
