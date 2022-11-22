package com.xiaomi.youpin.gwdash.bo;


import lombok.Data;

import java.io.Serializable;

@Data
public class OperationLogBo implements Serializable {

    private String appName;
    private String userName;
    private String dataId;
    private String dataBefore;
    private String dataAfter;
    private Long createTime;
    private Integer type;
    private String remark;
}
