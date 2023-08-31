package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeUserDeleteParam extends BaseParam implements Serializable {

    private Long id;
    private Long nodeId;
    private Long outId;
    private Integer outIdType;
    private String delAcc;
    private Integer delUserType;

    @Override
    public boolean argCheck() {
        if (id != null) {
            return true;
        }
        if (nodeId == null
                && (outId == null
                || outIdType == null
                || StringUtils.isBlank(delAcc)
                || delUserType == null)) {
            return false;
        }
        if (outIdType != null && OutIdTypeEnum.getEnum(outIdType) == null) {
            return false;
        }
        if (delUserType != null && UserTypeEnum.getEnum(delUserType) == null) {
            return false;
        }
        return true;
    }
}
