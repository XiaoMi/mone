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

package com.xiaomi.youpin.rpc.test.udp;

import com.xiaomi.data.push.udp.UdpClient;
import com.xiaomi.data.push.udp.UdpServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.stream.IntStream;


@Slf4j
public class UdpTest {


    @Test
    public void testServer() throws InterruptedException {
        UdpServer server = new UdpServer("127.0.0.1", 1234, msg -> msg + " --> " + System.currentTimeMillis());
        server.start();
    }


    @Test
    public void testClient() throws InterruptedException, IOException {
        UdpClient client = new UdpClient(1235, msg -> {
            log.info("--->{}", msg);
        });

        client.start();
        IntStream.range(0, 10).forEach(i -> {
            client.sendMessage("hi", new InetSocketAddress("127.0.0.1", 1234));
        });

        System.in.read();
    }
}
