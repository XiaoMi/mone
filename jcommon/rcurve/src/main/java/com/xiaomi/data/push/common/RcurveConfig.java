/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
