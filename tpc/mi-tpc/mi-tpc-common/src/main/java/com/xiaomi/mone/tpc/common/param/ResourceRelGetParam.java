package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import com.xiaomi.mone.tpc.common.enums.ResourceStatusEnum;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zhangxiaowei6
 * @date: 2022/3/28
 */
@Data
@ToString(callSuper = true)
public class ResourceRelGetParam extends BaseParam implements Serializable {
    private Long id;
    private boolean encrypted;

    @Override
    public boolean argCheck() {
        if (id == null || id <= 0) {
            return false;
        }
        return true;
    }
}
