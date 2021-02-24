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

package com.xiaomi.data.push.uds.codes;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.lang.reflect.Type;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2/4/21
 */
public class BytesCodes implements ICodes {

    @Override
    public <T> T decode(byte[] data, Type type) {
        ByteBuf buf = Unpooled.wrappedBuffer(data);
        return decode(buf, type);
    }


    public <T> T decode(ByteBuf buf, Type type) {
        if (type.equals(byte[][].class)) {
            int row = buf.readInt();
            byte[][] bytes = new byte[row][];
            for (int i = 0; i < row; i++) {
                int col = buf.readInt();
                byte[] bb = new byte[col];
                for (int j = 0; j < col; j++) {
                    bb[j] = buf.readByte();
                }
                bytes[i] = bb;
            }
            return (T) bytes;
        } else if (type.equals(boolean.class)) {
            return (T) Boolean.valueOf(buf.readByte() == 1);
        } else if (type.equals(int.class)) {
            return (T) (Integer.valueOf(buf.readInt()));
        } else if (type.equals(long.class)) {
            return (T) (Long.valueOf(buf.readLong()));
        } else if (type.equals(String.class)) {
            int len = buf.readInt();
            byte[] d = new byte[len];
            buf.readBytes(d);
            return (T) (new String(d));
        } else if (type.equals(String[].class)) {
            int len = buf.readInt();
            String[] strs = new String[len];
            IntStream.range(0, len).forEach(i -> {
                int l = buf.readInt();
                byte[] d = new byte[l];
                buf.readBytes(d);
                strs[i] = new String(d);
            });
            return (T) strs;
        }
        return null;
    }


    @Override
    public <T> byte[] encode(T t) {
        if (t.getClass().equals(byte[][].class)) {
            byte[][] bytes = (byte[][]) t;
            int row = bytes.length;
            CompositeByteBuf buffer = Unpooled.compositeBuffer(1 + 1 + row);
            buffer.addComponents(true, Unpooled.buffer(4).writeInt(row));
            for (int i = 0; i < row; i++) {
                int col = bytes[i].length;
                buffer.addComponents(true, Unpooled.buffer(4).writeInt(col));
                ByteBuf buf = Unpooled.buffer(row * col);
                for (int j = 0; j < col; j++) {
                    buf.writeByte(bytes[i][j]);
                }
                buffer.addComponents(true, buf);
            }
            int capacity = buffer.capacity();
            byte[] data = new byte[capacity];
            buffer.readBytes(data);
            return data;
        } else if (t.getClass().equals(boolean.class) || t.getClass().equals(Boolean.class)) {
            return ((Boolean) t) ? new byte[]{1} : new byte[]{0};
        } else if (t.getClass().equals(Integer.class)) {
            return int2Bytes((Integer) t);
        } else if (t.getClass().equals(Long.class) || t.getClass().equals(long.class)) {
            return long2Bytes((Long) t);
        } else if (t.getClass().equals(String.class)) {
            String str = (String) t;
            byte[] data = str.getBytes();
            byte[] len = int2Bytes(data.length);
            byte[] c = new byte[len.length + data.length];
            System.arraycopy(len, 0, c, 0, len.length);
            System.arraycopy(data, 0, c, len.length, data.length);
            return c;
        } else if (t.getClass().equals(String[].class)) {
            //字符串数组的编码
            String[] strs = (String[]) t;
            CompositeByteBuf buffer = Unpooled.compositeBuffer(1 + strs.length);
            buffer.addComponents(true, Unpooled.buffer(4).writeInt(strs.length));
            IntStream.range(0, strs.length).forEach(i -> {
                buffer.addComponents(true, Unpooled.buffer(4).writeInt(strs[i].length()));
                buffer.addComponents(true, Unpooled.buffer(strs[i].length()).writeBytes(strs[i].getBytes()));
            });
            int capacity = buffer.capacity();
            byte[] data = new byte[capacity];
            buffer.readBytes(data);
            return data;
        }

        return new byte[0];
    }


    private byte[] int2Bytes(int data) {
        return new byte[]{(byte) ((data >> 24) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff)};

    }

    private byte[] long2Bytes(long data) {
        return new byte[]{
                (byte) ((data >> 56) & 0xff),
                (byte) ((data >> 48) & 0xff),
                (byte) ((data >> 40) & 0xff),
                (byte) ((data >> 32) & 0xff),
                (byte) ((data >> 24) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff),
        };
    }

    @Override
    public byte type() {
        return 2;
    }
}
