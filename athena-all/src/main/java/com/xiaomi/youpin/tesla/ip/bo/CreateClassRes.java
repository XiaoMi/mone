package com.xiaomi.youpin.tesla.ip.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/18 09:49
 */
@Data
public class CreateClassRes implements Serializable {

    private String moduleName;

    private String packageStr;

    private String className;

    private int code;

}
