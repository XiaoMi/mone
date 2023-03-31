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
public class FlagEditParam extends BaseParam {

    private Long id;
    private String flagName;
    private String flagKey;
    private String flagVal;
    private String desc;
    private Integer type;

    @Override
    public boolean argCheck() {
        if (id == null) {
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
