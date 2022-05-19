package com.xiaomi.data.push.micloud.bo.response;

import lombok.Data;

import java.util.List;

@Data
public class OfflineResponse {
    long ticketId;
    List<Offline> result;
}
