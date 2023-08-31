package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zhangxiaowei6
 * @date: 2022/4/1
 */
@Data
@ToString(callSuper = true)
public class AddQuotaParam implements ArgCheck, Serializable {

    private String dubboGroup;
    private String dubboVersion;
    private String nocasAddr;

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(nocasAddr)) {
            return false;
        }
        return true;
    }
}
