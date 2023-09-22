/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.rpc.processor.service;

import cn.hutool.core.util.ObjectUtil;
import com.google.gson.Gson;
import com.xiaomi.mone.log.agent.channel.ChannelDefine;
import com.xiaomi.mone.log.agent.channel.locator.ChannelDefineRpcLocator;
import com.xiaomi.mone.log.api.model.msg.LineMessage;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/8/5 14:18
 */
@Slf4j
public class RpcTest {

    @Test
    public void testRpc() {
        Ioc.ins().init("com.xiaomi");
        ChannelDefineRpcLocator channelDefineRpcLocator = Ioc.ins().getBean(ChannelDefineRpcLocator.class);
        System.out.println(channelDefineRpcLocator);
        List<ChannelDefine> channelDefine = channelDefineRpcLocator.getChannelDefine();
        log.info("Returns data：{}", channelDefine);
        Assert.assertNotNull(channelDefine);
    }

    @Test
    public void test() {
        Ioc.ins().init("com.xiaomi");
        ChannelDefineRpcLocator channelDefineRpcLocator = Ioc.ins().getBean(ChannelDefineRpcLocator.class);
        List<ChannelDefine> logCollectMetaFromManager = channelDefineRpcLocator.getChannelDefine("127.0.0.1");
        System.out.println(new Gson().toJson(logCollectMetaFromManager));
        Assert.assertNotNull(logCollectMetaFromManager);

    }

    @Test
    public void testCopy() {
        LineMessage lineMessage = new LineMessage();
        lineMessage.setLineNumber(2L);
        List<LineMessage> messageList = Arrays.asList(lineMessage);
        List<LineMessage> cloneList = messageList.stream().map(message -> ObjectUtil.clone(message)).collect(Collectors.toList());
//        List<LineMessage> cloneList = messageList.stream().collect(Collectors.toList());
        log.info("return data：{}", cloneList);
        Assert.assertNotNull(cloneList);
    }


    @Test
    public void test1() throws Exception {

//        ChannelEngine channelEngine = new ChannelEngine();
//        TalosOutput talosOutput = new TalosOutput();
//        talosOutput.setTopic("519_planet-opmsg-postprocess_191_china");
//        TalosProducer talosProducer = channelEngine.initTalosProducer(talosOutput, "");
//        TalosProducer talosProducer2 = channelEngine.initTalosProducer(talosOutput, "");
//        List<Message> messages = Lists.newArrayList();
//        Message message = new Message(ByteBuffer.wrap("helllo ！！！！！！".getBytes(StandardCharsets.UTF_8)));
//        messages.add(message);
//        IntStream.range(0, 10).forEach(value -> {
//            try {
//                talosProducer.addUserMessage(messages);
//            } catch (ProducerNotActiveException e) {
//                log.error("", e);
//            }
//        });
//        List<Message> messages2 = Lists.newArrayList();
//        Message message2 = new Message(ByteBuffer.wrap("test test！！！！！！".getBytes(StandardCharsets.UTF_8)));
//        messages2.add(message2);
//        talosProducer2.addUserMessage(messages2);
//        talosProducer.shutdown();
//        talosProducer2.addUserMessage(messages2);
//        talosProducer.addUserMessage(messages);
//        System.in.read();
    }
}
