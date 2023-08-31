package com.xiaomi.mone.tpc.common.param;

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
public class FlagQryOneParam extends BaseParam {

    private Long id;
    private Long parentId;
    private String flagName;
    private String flagKey;
    private Integer type;

    @Override
    public boolean argCheck() {
        if (parentId == null) {
            return false;
        }
        if (StringUtils.isBlank(flagName)) {
            return false;
        }
        if (StringUtils.isBlank(flagKey)) {
            return false;
        }
        return true;
    }
}
