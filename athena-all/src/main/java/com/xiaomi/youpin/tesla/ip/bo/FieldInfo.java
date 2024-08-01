package com.xiaomi.youpin.tesla.ip.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/6/21 16:26
 */
@Data
public class FieldInfo implements Serializable {

    private String name;

    private String classType;

    private Map<String,String> meta;

    private List<AnnoInfo> annoList;

}
