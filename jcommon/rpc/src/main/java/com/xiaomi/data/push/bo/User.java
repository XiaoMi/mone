package com.xiaomi.data.push.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 *
 * 用户信息
 */
@Data
public class User implements Serializable {

    private String name;

    private String type;

    private Map<String, String> attachments;
}
