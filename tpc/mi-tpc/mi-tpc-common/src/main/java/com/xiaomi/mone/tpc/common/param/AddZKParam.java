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
public class AddZKParam implements ArgCheck, Serializable {

    private Integer threads;
    private String name;
    private String type;
    private Integer id;
    private String dataSourceUrl;

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(dataSourceUrl)) {
            return false;
        }
        if (threads == null || threads <= 0) {
            return false;
        }
        return true;
    }
}
