package com.xiaomi.mone.app.model.vo;

import com.xiaomi.mone.app.model.BaseCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description 变更的mq对象
 * @date 2023/2/14 20:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeraEnvIpVo extends BaseCommon {
    private Long id;

    private Long heraAppId;

    private Long appId;

    private String appName;

    private Long envId;

    private String envName;

    private List<String> ipList;

}
