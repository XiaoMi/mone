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

package com.xiaomi.youpin.tesla.file.server.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;


@Slf4j
public class FSCommonTest {


    @Test
    public void testSaveFile() throws IOException {
        byte[] data = Files.readAllBytes(Paths.get("/tmp/data/download/test"));
        ByteBuf buf = Unpooled.wrappedBuffer(data);
//        saveFile(buf);


        saveFile2(buf, data.length);
    }

    //644
    private void saveFile(ByteBuf buf) throws IOException {
        long begin = System.currentTimeMillis();
        FileOutputStream foStream = new FileOutputStream("/tmp/a");
        byte[] dst = new byte[buf.readableBytes()];
        buf.readBytes(dst);
        foStream.write(dst);
        foStream.flush();
        System.out.println(System.currentTimeMillis() - begin);
    }


    private void saveFile2(ByteBuf buffer, int size) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(new File("/tmp/b"));
        try {
            FileChannel localfileChannel = outputStream.getChannel();
            ByteBuffer byteBuffer = buffer.nioBuffer();
            int written = 0;
            while (written < size) {
                written += localfileChannel.write(byteBuffer);
            }
            buffer.readerIndex(buffer.readerIndex() + written);
            localfileChannel.force(false);
        } finally {
            outputStream.close();
        }
    }


}
