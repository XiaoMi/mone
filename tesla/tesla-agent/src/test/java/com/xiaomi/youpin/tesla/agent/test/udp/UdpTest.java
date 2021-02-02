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

package com.xiaomi.youpin.tesla.agent.test.udp;

import com.google.gson.Gson;
import com.xiaomi.data.push.udp.UdpClient;
import com.xiaomi.youpin.tesla.agent.bo.MqCommand;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.common.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;


@Slf4j
public class UdpTest {


    @Test
    public void testUdp() throws InterruptedException, IOException {
        String host = NetUtils.getLocalHost();
        UdpClient client = new UdpClient(0, msg -> {
            log.info("{}", msg.getMessage());
        });

        client.start();

        MqCommand mqCommand = new MqCommand();
        mqCommand.setCmd(AgentCmd.debugReq);
        mqCommand.setBody("");

        client.sendMessage(new Gson().toJson(mqCommand), new InetSocketAddress(host, 9799));

        System.in.read();
    }
}
