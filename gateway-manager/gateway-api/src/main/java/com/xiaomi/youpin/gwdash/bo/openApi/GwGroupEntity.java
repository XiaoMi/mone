package com.xiaomi.youpin.gwdash.bo.openApi;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jiangzheng3
 * @version 1.0
 * @description: 网关分组实体
 * @date 2022/2/28 17:02
 */
@Data
public class GwGroupEntity implements Serializable {

    private int id;

    private  String name;

    private  String description;

}
