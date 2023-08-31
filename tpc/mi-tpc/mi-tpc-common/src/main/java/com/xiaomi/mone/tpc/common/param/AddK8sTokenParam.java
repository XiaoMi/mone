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
public class AddK8sTokenParam implements ArgCheck, Serializable {

    private String url;
    private String token;

    @Override
    public void encrypted() {
        if (StringUtils.isNotBlank(token)) {
            token = "******";
        }
    }

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        if (StringUtils.isBlank(token)) {
            return false;
        }
        return true;
    }
}
