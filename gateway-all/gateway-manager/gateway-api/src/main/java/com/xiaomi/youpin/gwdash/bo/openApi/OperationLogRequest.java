package com.xiaomi.youpin.gwdash.bo.openApi;

import lombok.Data;

import java.io.Serializable;

@Data
public class OperationLogRequest extends RequestParam  implements Serializable {

    private String appName;
    private String userName;
    private String dataId;
    private String dataBefore;
    private String dataAfter;
    private String createTime;
    private int type;
    private String remark;



}
