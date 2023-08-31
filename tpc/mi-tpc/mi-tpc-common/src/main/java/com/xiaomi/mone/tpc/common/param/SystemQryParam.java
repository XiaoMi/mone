package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.SystemStatusEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class SystemQryParam extends BaseParam {

    private Long id;
    private String systemName;
    private Integer status;

    @Override
    public boolean argCheck() {
        if (status != null && SystemStatusEnum.getEnum(status) == null) {
            return false;
        }
        return true;
    }
}
