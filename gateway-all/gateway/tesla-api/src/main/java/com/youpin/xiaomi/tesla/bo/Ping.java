package com.youpin.xiaomi.tesla.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2022/10/5 11:29
 */
@Data
public class Ping implements Serializable {

    private String data;

    private long time;

}
