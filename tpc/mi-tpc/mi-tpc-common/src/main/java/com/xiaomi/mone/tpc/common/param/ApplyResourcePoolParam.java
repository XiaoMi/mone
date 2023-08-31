package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.ResourceTypeEnum;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApplyResourcePoolParam implements ArgCheck {

    private Long resourceId;
    private Integer type;

    @Override
    public boolean argCheck() {
        if (type == null || ResourceTypeEnum.getEnum(type) == null) {
            return false;
        }
        return true;
    }
}
