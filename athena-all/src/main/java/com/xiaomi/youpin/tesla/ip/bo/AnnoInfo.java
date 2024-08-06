package com.xiaomi.youpin.tesla.ip.bo;

import lombok.Data;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/6/21 17:03
 */
@Data
public class AnnoInfo {

    private String name;

    private Map<String,AnnoMember> members;

}
