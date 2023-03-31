package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApplyResPointerParam implements ArgCheck {
    private Long resourceId;

    @Override
    public boolean argCheck() {
        if (resourceId == null) {
            return false;
        }
        return true;
    }
}
