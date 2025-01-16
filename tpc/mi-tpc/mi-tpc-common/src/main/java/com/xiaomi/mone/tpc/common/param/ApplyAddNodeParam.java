package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Data
@ToString
public class ApplyAddNodeParam implements ArgCheck {
    private Integer type;
    private String nodeName;
    private String desc;
    private OrgInfoParam orgParam;
    private String code;
    private Map<String, String> env;

    @Override
    public boolean argCheck() {
        if (type == null || NodeTypeEnum.getEnum(type) == null) {
            return false;
        }
        if (StringUtils.isBlank(nodeName)) {
            return false;
        }
        if (orgParam != null && !orgParam.argCheck()) {
            return false;
        }
        if (NodeTypeEnum.PRO_GROUP_TYPE.getCode().equals(type) && StringUtils.isBlank(code)) {
            return false;
        }
        return true;
    }
}
