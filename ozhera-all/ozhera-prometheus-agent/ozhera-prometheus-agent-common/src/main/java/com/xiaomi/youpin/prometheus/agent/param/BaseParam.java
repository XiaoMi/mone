package com.xiaomi.youpin.prometheus.agent.param;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public  class BaseParam  implements ArgCheck, Serializable {

    @Override
    public boolean argCheck() {
        return false;
    }
}
