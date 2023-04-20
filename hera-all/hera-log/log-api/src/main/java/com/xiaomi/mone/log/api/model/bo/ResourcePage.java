package com.xiaomi.mone.log.api.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: wtt
 * @Date: 2022/5/12 14:54
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourcePage {
    private Integer resourceCode;
    private String regionEnCode;
    private String aliasName;
    private Integer page = 1;
    private Integer pageSize = 10;
}
