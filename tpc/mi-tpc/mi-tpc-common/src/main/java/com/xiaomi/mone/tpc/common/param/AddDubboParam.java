package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class AddDubboParam implements ArgCheck , Serializable {

    private Integer threads;
    private String name;
    private String type;
    private String appName;
    private String regAddress;
    private String apiPackage;
    private Integer id;

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(appName)) {
            return false;
        }
        if (StringUtils.isBlank(regAddress)) {
            return false;
        }
        if (StringUtils.isBlank(apiPackage)) {
            return false;
        }
        if (threads == null || threads <= 0) {
            return false;
        }
        return true;
    }
}
