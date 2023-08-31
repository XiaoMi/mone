package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeMoveParam extends BaseParam {

    private Long fromId;
    private Long toId;

    @Override
    public boolean argCheck() {
        if (fromId == null) {
            return false;
        }
        if (toId == null) {
            return false;
        }
        return true;
    }
}
