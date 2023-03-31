package com.xiaomi.mone.tpc.login.common.param;

import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NullParam extends BaseParam {

    @Override
    public boolean argCheck() {
        return true;
    }
}
