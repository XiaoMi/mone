package com.xiaomi.data.push.micloud.bo.request;

import lombok.Data;

@Data
public class OfflineRequest {

    boolean skipTicket;
    boolean skipCheckOffline;
    long ticketId;
    String [] hostnames;
}
