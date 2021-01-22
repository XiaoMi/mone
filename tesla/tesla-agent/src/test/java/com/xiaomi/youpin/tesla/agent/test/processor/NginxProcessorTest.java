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
import com.xiaomi.youpin.tesla.agent.po.NginxReq;
import com.xiaomi.youpin.tesla.agent.processor.NginxProcessor;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class NginxProcessorTest {


    @Test
    public void testNginxProcessor() throws Exception {
        NginxProcessor nginxProcessor = new NginxProcessor();
        RemotingCommand cmd = new RemotingCommand();
        NginxReq req = new NginxReq();
        req.setCmd("info");
        cmd.setBody(new Gson().toJson(req).getBytes());
        nginxProcessor.processRequest(null, cmd);
    }


    @Test
    public void testNginxProcessorStart() throws Exception {
        NginxProcessor nginxProcessor = new NginxProcessor();
        RemotingCommand cmd = new RemotingCommand();
        NginxReq req = new NginxReq();
        req.setCmd("start");
        cmd.setBody(new Gson().toJson(req).getBytes());
        nginxProcessor.processRequest(null, cmd);
    }


    @Test
    public void testNginxProcessorStop() throws Exception {
        NginxProcessor nginxProcessor = new NginxProcessor();
        RemotingCommand cmd = new RemotingCommand();
        NginxReq req = new NginxReq();
        req.setCmd("stop");
        cmd.setBody(new Gson().toJson(req).getBytes());
        nginxProcessor.processRequest(null, cmd);
    }


    @Test
    public void testNginxProcessorReload() throws Exception {
        NginxProcessor nginxProcessor = new NginxProcessor();
        RemotingCommand cmd = new RemotingCommand();
        NginxReq req = new NginxReq();
        req.setCmd("reload");
        cmd.setBody(new Gson().toJson(req).getBytes());
        nginxProcessor.processRequest(null, cmd);
    }


    @Test
    public void testNginxProcessorModifyConfig() throws Exception {
        NginxProcessor nginxProcessor = new NginxProcessor();
        RemotingCommand cmd = new RemotingCommand();
        NginxReq req = new NginxReq();
        req.setCmd("modifyConfig");
        req.setConfigPath("/usr/local/etc/nginx/nginx.conf");
        req.setConfigStr(new String(Files.readAllBytes(Paths.get("/tmp/nginx.conf"))));
        cmd.setBody(new Gson().toJson(req).getBytes());
        nginxProcessor.processRequest(null, cmd);
    }


}
