package com.xiaomi.data.push.common;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 1/22/21
 */
public class RcurveConfig {

    @Getter
    @Setter
    private byte codeType = 1;

    private static class LazyHolder {
        private static RcurveConfig ins = new RcurveConfig();
    }

    public void init(Consumer<RcurveConfig> consumer) {
        consumer.accept(this);
    }

    public static RcurveConfig ins() {
        return LazyHolder.ins;
    }


}
