package com.xiaomi.data.push.monitor.model;

import lombok.Data;

/**
 * @author maojinrui
 */
@Data
public class CreateChatGroupResponse {

    private Integer errcode;
    private String errmsg;
    private String chatid;
}
