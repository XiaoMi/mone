package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zhangxiaowei6
 * @date: 2022/3/25
 */
@Data
@ToString(callSuper = true)
public class ResourceDelRelParam extends BaseParam implements Serializable {
    private Long id;        //id
    private Long resourceId;
    @Override
    public boolean argCheck() {
        if (id == null || id <= 0) {
            return false;
        }
        if (resourceId == null || resourceId <= 0) {
            return false;
        }
        return true;
    }
}
