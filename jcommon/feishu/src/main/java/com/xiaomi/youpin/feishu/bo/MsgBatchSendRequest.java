package com.xiaomi.youpin.feishu.bo;

import lombok.Data;

import java.util.List;

@Data
public class MsgBatchSendRequest {
    private List<String> department_ids;
    private List<String> open_ids;
    private List<String> user_ids;
    private String msg_type="text";
    private ContentBo content;
}
