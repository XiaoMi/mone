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
public class ApplyEditParam extends BaseApplyParam {

    private Long id;
    private String applyName;
    private String desc;

    @Override
    public boolean argCheckV2() {
        if (id == null) {
            return false;
        }
        if (StringUtils.isBlank(applyName)) {
            return false;
        }
        return true;
    }
}
