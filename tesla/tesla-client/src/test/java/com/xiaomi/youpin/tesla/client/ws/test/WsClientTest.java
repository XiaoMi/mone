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

package com.xiaomi.youpin.tesla.client.ws.test;

import com.xiaomi.youpin.tesla.client.ws.WsClient;
import org.junit.Test;

import java.net.URI;

public class WsClientTest {


    @Test
    public void testSend() throws Exception {
        WsClient client = new WsClient();
//        client.setUri(new URI("ws://xxxxx/ws"));
        client.init(new URI("ws://xxxx/ws"), (msg) -> {
            System.out.println(msg);
        }, () -> "{'uri':'/aaaa/bbb'}");
        client.connect();
        client.send("{'uri':'/mtop/arch/im','cmd':'test'}");
        System.in.read();
    }
}
