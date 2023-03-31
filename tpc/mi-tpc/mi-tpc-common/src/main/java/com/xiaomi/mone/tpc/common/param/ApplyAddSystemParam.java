package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.SystemStatusEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Data
@ToString
public class ApplyAddSystemParam implements ArgCheck {
    private String systemName;
    private Integer status;
    private String desc;

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(systemName)) {
            return false;
        }
        if (status == null || SystemStatusEnum.getEnum(status) == null) {
            return false;
        }
        return true;
    }
}
