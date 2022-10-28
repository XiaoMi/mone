package com.xiaomi.youpin.gwdash.bo.openApi;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangzheng3
 * @version 1.0
 * @description: 网关用户信息
 * @date 2022/2/28 17:00
 */
@Data
public class GwUser implements Serializable {

    private Long id;

    private String userName;

    private String gid;

    private List<GwGroupEntity> gidInfos = new ArrayList<>();

}
