package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.ApplyTypeEnum;
import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class ApplyAddParam extends BaseApplyParam {

    private Long outId;
    private Integer outIdType;
    private Long nodeId;
    private String applyName;
    private String desc;

    @Override
    public boolean argCheckV2() {
        if (!ApplyTypeEnum.SYSTEM_APPLY.getCode().equals(getType())) {
            if ((nodeId == null) && (outId == null || OutIdTypeEnum.getEnum(outIdType) == null)) {
                return false;
            }
        }
        if (StringUtils.isBlank(applyName)) {
            return false;
        }
        return true;
    }
}
