package com.xiaomi.youpin.tesla.ip.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author caobaoyu
 * @description:
 * @date 2023-06-07 14:18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AthenaReq {

    private String aiProxy;

    private String userName;

    private String zzToken;

    private String version;

    private String os;

    private String ideaVersion;

    private String time;


}
