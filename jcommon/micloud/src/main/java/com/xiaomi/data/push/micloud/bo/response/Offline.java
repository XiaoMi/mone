package com.xiaomi.data.push.micloud.bo.response;

import lombok.Data;

@Data
public class Offline {
    String hostname;
    String message;
    boolean success;
}
