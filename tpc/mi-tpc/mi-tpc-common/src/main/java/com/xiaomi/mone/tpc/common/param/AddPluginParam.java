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
public class AddPluginParam implements ArgCheck , Serializable {

    private String name;
    private String jarPath;
    private String type;
    private String iocPackage;
    private Integer id;


    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(jarPath)) {
            return false;
        }
        if (StringUtils.isBlank(iocPackage)) {
            return false;
        }
        return true;
    }
}
