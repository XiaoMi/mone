package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.ApplyTypeEnum;
import com.xiaomi.mone.tpc.common.enums.ApprovalStatusEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class ApprovalQryParam extends BaseParam {

    private Long id;
    private Long applyId;
    private Integer type;
    private Integer status;
    private String approvalName;
    private Long nodeId;
    private boolean myApproval;

    @Override
    public boolean argCheck() {
        if (type != null && ApplyTypeEnum.getEnum(type) == null) {
            return false;
        }
        if (status != null && ApprovalStatusEnum.getEnum(status) == null) {
            return false;
        }
        return true;
    }
}
