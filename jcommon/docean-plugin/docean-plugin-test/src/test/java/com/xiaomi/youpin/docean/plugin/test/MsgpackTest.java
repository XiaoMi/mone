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

package com.xiaomi.youpin.docean.plugin.test;

import lombok.Data;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.annotation.Message;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/13 15:49
 */
public class MsgpackTest {

    @Message
    @lombok.Data
    public static class Data {
        private int id;
        private String name;
    }



    @Test
    public void testMsgpack() throws IOException {
        MessagePack mp = new MessagePack();
        Data d = new Data();
        d.setId(12);
        d.setName("zzy");
        byte[] b =mp.write(d);
        System.out.println(Arrays.toString(b));
        Data dd = mp.read(b,Data.class);
        System.out.println(dd);
        String str = "a12";
        System.out.println(mp.read(mp.write(str),String.class));
        int i =12;
        System.out.println(mp.read(mp.write(i),int.class));
    }

}
