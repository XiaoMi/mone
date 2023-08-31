package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.ApplyStatusEnum;
import com.xiaomi.mone.tpc.common.enums.ApplyTypeEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class ApplyQryParam extends BaseParam {

    private Long id;
    private Long nodeId;
    private Integer type;
    private Integer status;
    private String applyName;
    private boolean myApply = true;//我的申请或待审批

    @Override
    public boolean argCheck() {
        if (type != null && ApplyTypeEnum.getEnum(type) == null) {
            return false;
        }
        if (status != null && ApplyStatusEnum.getEnum(status) == null) {
            return false;
        }
        return true;
    }
}
