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
import com.xiaomi.youpin.tesla.agent.po.DockerReq;
import com.xiaomi.youpin.tesla.agent.po.DockerRes;
import com.xiaomi.youpin.tesla.agent.processor.DockerProcessor;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.regex.Pattern;

public class DockerProcessorTest {


    @Test
    public void testDocker() {
        DockerProcessor processor = new DockerProcessor(null);
        DockerReq req = new DockerReq();
        req.setCmd("info");
        ChannelHandlerContext context = null;
        RemotingCommand req2 = RemotingCommand.createRequestCommand(1);
        req2.setBody(new Gson().toJson(req).getBytes());
        RemotingCommand res = processor.processRequest(context, req2);
        System.out.println(new String(res.getBody()));
    }


    @Test
    public void testSaveDockerFile() {
        DockerProcessor p = new DockerProcessor(null);
        DockerReq dr = new DockerReq();
        dr.setJvmParams("-Xms100M   -Xmx100M");
        dr.setServicePath("/tmp/");
        dr.setJarName("aa.jar");
//        p.saveDockerFile(dr, "/tmp/zz");
    }


    @Test
    public void testNuke() {
        String imageName = "dfztest1-gg-20200521143055133";
        String name = getName(imageName);
//        String name = "dfzt";
        System.out.println(imageName.startsWith(name));
        boolean res = Pattern.matches("^(cr\\.d\\.xiaomi\\.net/mixiao(-st)?/)?" + Pattern.quote(name) + "[:-][0-9]+$", imageName);
        System.out.println(res);
        Assert.assertTrue(res);
    }


    private String getName(String jarName) {
        int lastIndex = jarName.lastIndexOf("-");
        if (lastIndex != -1) {
            return jarName.substring(0, lastIndex);
        }
        return jarName;
    }
}
