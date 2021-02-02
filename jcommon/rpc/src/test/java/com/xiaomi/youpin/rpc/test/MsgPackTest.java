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

package com.xiaomi.youpin.rpc.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaomi.data.push.bo.MPPing;
import org.junit.Test;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.util.Arrays;

public class MsgPackTest {


    @Test
    public void test1() throws IOException {
        System.out.println("tests");

        MPPing ping = new MPPing();
        ping.setData("ping");
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        byte[] data = objectMapper.writeValueAsBytes(ping);
        System.out.println(Arrays.toString(data));


        MPPing v = objectMapper.readValue(data, MPPing.class);
        System.out.println(v.getData());


    }
}
