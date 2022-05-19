package com.xiaomi.data.push.bo;

import lombok.Data;

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Data
public class AdminReq {

    private String cmd;

    private Map<String,String> params;

}
