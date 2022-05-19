package com.xiaomi.data.push.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class MPPing implements Serializable {

    private String data;

    private User user;
}
