package com.xiaomi.youpin.tesla.ip.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/29 14:51
 */
@Data
@Builder
public class ParamDialogReq implements Serializable {

    private String title;


}
