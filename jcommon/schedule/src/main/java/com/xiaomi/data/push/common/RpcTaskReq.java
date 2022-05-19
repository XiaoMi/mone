package com.xiaomi.data.push.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Data
public class RpcTaskReq implements Serializable {

    private String cmd;

    private Integer taskId;

    private Map<String, String> attachments;

}
