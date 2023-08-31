package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApplyAddMemberParam implements ArgCheck {
    private Integer type;

    @Override
    public boolean argCheck() {
        if (type == null || NodeUserRelTypeEnum.getEnum(type) == null) {
            return false;
        }
        return true;
    }
}
