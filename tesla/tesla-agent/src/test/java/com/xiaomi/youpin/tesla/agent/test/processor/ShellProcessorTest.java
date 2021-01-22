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

package com.xiaomi.youpin.tesla.agent.test.processor;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.tesla.agent.po.ShellReq;
import com.xiaomi.youpin.tesla.agent.processor.ShellProcessor;
import org.junit.Test;

public class ShellProcessorTest {


    @Test
    public void testInit() {
        ShellProcessor shellProcessor = new ShellProcessor();
        RemotingCommand cmd = new RemotingCommand();
        ShellReq req = new ShellReq();
        req.setPath("xxxx/");
        req.setShellCmd("__init__");
        req.setCmd("ssh-rsa 0000000 work@");
        req.setUser("zhangzhiyong");
        cmd.setBody(new Gson().toJson(req).getBytes());
        shellProcessor.processRequest(null,cmd);
    }
}
