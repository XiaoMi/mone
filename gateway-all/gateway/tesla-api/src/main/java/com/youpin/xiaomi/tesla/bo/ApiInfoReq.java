package com.youpin.xiaomi.tesla.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2022/9/30 15:10
 */
@Data
public class ApiInfoReq implements Serializable {

    private int pageNum;

    private int pageSize;

    private String tenant;

    private long lastUpdateTime;


}
