package com.xiaomi.youpin.tesla.ip.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/5/31 10:29
 */
@Data
public class Msg implements Serializable {

    private String role;

    private String content;

}
