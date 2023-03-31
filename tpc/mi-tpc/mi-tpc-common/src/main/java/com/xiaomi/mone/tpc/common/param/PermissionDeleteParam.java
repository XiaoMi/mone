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
public class PermissionDeleteParam extends BaseParam {

    private Long id;

    @Override
    public boolean argCheck() {
        if (id == null) {
            return false;
        }
        return true;
    }
}
