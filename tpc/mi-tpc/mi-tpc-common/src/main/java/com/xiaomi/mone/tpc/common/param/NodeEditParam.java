package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeEditParam extends BaseParam implements Serializable {

    private Long id;
    private Long outId;
    private Integer outIdType;
    private String nodeName;
    private String desc;
    private OrgInfoParam orgParam;
    private String code;
    private Map<String,String> env;

    @Override
    public boolean argCheck() {
        if (id == null && (outId == null || outId.equals(0L))) {
            return false;
        }
        if (id == null && outIdType != null && OutIdTypeEnum.getEnum(outIdType) == null) {
            return false;
        }
        if (StringUtils.isBlank(nodeName)){
            return false;
        }
        if (orgParam != null && !orgParam.argCheck()) {
            return false;
        }
        return true;
    }
}
