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

package com.xiaomi.youpin.gateway.common.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
public class ByteBufTest {


    @Test
    public void testByteBuf() {
        ByteBuf buf = Unpooled.directBuffer(10);
        buf.writeShort(2);
        buf.writeLong(1L);

        byte[] data = new byte[10];
        ByteBuf buf2 = buf.duplicate();
        buf2.writerIndex(0);
        buf2.writeShort(3);
        buf2.writeLong(1L);
        buf2.readBytes(data);
        System.out.println(Arrays.toString(data));

        System.out.println(buf.readerIndex());
        buf.readBytes(data);
        System.out.println(buf.readerIndex());
        buf.readerIndex(0);
        buf.readBytes(data);
        System.out.println(Arrays.toString(data));
    }

    @Test
    public void testByteBuff2() {
        ByteBuf buf = Unpooled.directBuffer(2);
        buf.writeByte((byte)'{');
        buf.writeByte((byte)'[');
        System.out.println(buf.readableBytes());

        System.out.println((char)buf.getByte(1));

        System.out.println((char)buf.getByte(buf.readableBytes()-1));

        System.out.println((char)buf.readByte());
    }

}
