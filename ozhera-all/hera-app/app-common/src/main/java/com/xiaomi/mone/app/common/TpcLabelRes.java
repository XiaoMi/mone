package com.xiaomi.mone.app.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wtt
 * @version 1.0
 * @description tpc标签页返回值
 * @date 2023/8/16 16:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TpcLabelRes {
    private Long id;
    private Integer type;
    private Integer status;
    private String desc;
    private String content;
    private Long createrId;
    private String createrAcc;
    private Integer createrType;
    private Long updaterId;
    private String updaterAcc;
    private Integer updaterType;
    private Long createTime;
    private Long updateTime;
    private Integer parentId;
    private String flagName;
    private String flagKey;
    private String flagVal;
    private Long parentIdV2;

}
