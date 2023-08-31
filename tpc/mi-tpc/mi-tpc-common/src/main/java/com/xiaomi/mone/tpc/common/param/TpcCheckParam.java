package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
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
public class TpcCheckParam extends BaseParam {

    private String path = null;
    private String system = null;
    private String token = null;
    private Long nodeId = null;
    private Long outId = null;
    private Integer outIdType = null;

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(system)) {
            return false;
        }
        if (StringUtils.isBlank(token)) {
            return false;
        }
        if (StringUtils.isBlank(getAccount()) || UserTypeEnum.getEnum(getUserType()) == null) {
            return false;
        }
        if (StringUtils.isBlank(path)) {
            return false;
        }
        if (nodeId == null && (outId == null || outIdType == null)) {
            return false;
        }
        if (outIdType != null && OutIdTypeEnum.getEnum(outIdType) == null) {
            return false;
        }
        return true;
    }
}
