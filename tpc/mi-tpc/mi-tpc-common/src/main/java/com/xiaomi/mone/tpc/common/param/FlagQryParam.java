package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.FlagTypeEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class FlagQryParam extends BaseParam {

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
        return true;
    }
}
