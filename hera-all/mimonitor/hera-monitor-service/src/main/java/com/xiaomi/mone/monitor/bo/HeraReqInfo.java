package com.xiaomi.mone.monitor.bo;

import com.xiaomi.mone.monitor.dao.model.HeraOperLog;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/1/13 16:16
 */
@Data
@ToString
@Builder
public class HeraReqInfo {
    private String moduleName;
    private String interfaceName;
    private String reqUrl;
    private String user;
    private HeraOperLog operLog;
}
