package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangxiaowei
 * @date 2022/4/24
 */
@Data
public class MutiGrafanaResponse  implements Serializable {
    Integer code;
    String message;
    List<GrafanaResponse> data;
    String url;
}
