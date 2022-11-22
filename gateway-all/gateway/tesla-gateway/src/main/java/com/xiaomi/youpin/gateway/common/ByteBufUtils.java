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

package com.xiaomi.youpin.gateway.common;

import com.xiaomi.youpin.gateway.filter.FilterContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public abstract class ByteBufUtils {


    public static ByteBuf createBuf(FilterContext ctx, String content, boolean allowDirectBuf) {
        if (!allowDirectBuf) {
            return Unpooled.wrappedBuffer(content.getBytes());
        }
        try {
            return ctx.byteBuf(content.getBytes(), true);
        } catch (Throwable ex) {
            log.warn("alloc direct bytebuf error:{}", ex.getMessage());
            return Unpooled.wrappedBuffer(content.getBytes());
        }
    }
}
