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

package com.xiaomi.youpin.tesla.test;

import com.xiaomi.youpin.gateway.netty.filter.CodeParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
public class CodeParserTest {


    @Test
    public void testParser() {
        ByteBuf buf = Unpooled.wrappedBuffer("{\"msg\":0,\"code\":300}".getBytes());
        int res = CodeParser.parseCode(buf);
        System.out.println(res);
    }
}
