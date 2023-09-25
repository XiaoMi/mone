package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeStatusEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
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
public class NodeQryByFlagParam extends BaseParam implements Serializable {

    private Integer type;
    private Integer status;
    private String flagKey;

    @Override
    public boolean argCheck() {
        if (type == null || NodeTypeEnum.getEnum(type) == null) {
            return false;
        }
        if (status != null && NodeStatusEnum.getEnum(status) == null) {
            return false;
        }
        if (StringUtils.isBlank(flagKey)) {
            return false;
        }
        return true;
    }
}
