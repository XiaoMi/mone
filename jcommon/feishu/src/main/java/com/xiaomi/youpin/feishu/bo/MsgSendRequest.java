package com.xiaomi.youpin.feishu.bo;

import lombok.Data;

import java.util.Map;

@Data
public class MsgSendRequest {
    private String open_id;
    private String root_id;
    private String chat_id;
    private String user_id;
    private String email;
    private String msg_type;
    private MsgDetail content;
    private boolean update_multi = true;
    private Map<String, Object> card;
}
