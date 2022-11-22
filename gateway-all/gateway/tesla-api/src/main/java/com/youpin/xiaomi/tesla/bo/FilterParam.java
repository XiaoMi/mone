package com.youpin.xiaomi.tesla.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2022/10/4 11:26
 */
@Data
public class FilterParam implements Serializable {

    private String name;

    private String groups;

    private String type;

}
