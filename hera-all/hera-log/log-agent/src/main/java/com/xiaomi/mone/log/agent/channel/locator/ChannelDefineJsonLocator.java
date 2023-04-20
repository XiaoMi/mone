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

package com.xiaomi.mone.log.agent.channel.locator;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.log.agent.channel.ChannelDefine;
import com.xiaomi.mone.log.agent.common.AbstractElementAdapter;
import com.xiaomi.mone.log.agent.channel.conf.AgentTailConf;
import com.xiaomi.mone.log.agent.output.Output;
import com.xiaomi.mone.log.agent.input.Input;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 本地json配置方式获取channel元数据
 *
 * @author shanwb
 * @date 2021-07-21
 */
@Slf4j
public class ChannelDefineJsonLocator implements ChannelDefineLocator {
    private static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Input.class, new AbstractElementAdapter());
        gsonBuilder.registerTypeAdapter(Output.class, new AbstractElementAdapter());
        gson = gsonBuilder.create();
    }

    @Override
    public List<ChannelDefine> getChannelDefine() {
        try {
            AgentTailConf agentTailConf = gson.fromJson(readConfigJson(), new TypeToken<AgentTailConf>() {
            }.getType());
            return ChannelDefineRpcLocator.agentTail2ChannelDefine(agentTailConf);
        } catch (Exception e) {
            log.error("ChannelDefineJsonLocator getChannelDefine exception:{}", e);
        }
        return Lists.newArrayList();
    }

    @Override
    public List<ChannelDefine> getChannelDefine(String ip) {
        return null;
    }

    private String readConfigJson() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(ChannelDefineJsonLocator.class.getClassLoader().getResourceAsStream("agent_channel_config.json")));
            StringBuffer sb = new StringBuffer();
            String line;
            while (null != (line = in.readLine())) {
                sb.append("\n" + line);
            }
            log.warn("ChannelDefineJsonLocator:{}", sb.toString());
            String str = sb.toString().replaceAll("\r|\n|\\s", "");
            return str;
        } catch (IOException e) {
            log.error("readConfigJson IOException:{}", e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        String pathname = ChannelDefineJsonLocator.class.getClassLoader().getResource("agent_channel_config.json").getPath();
//        Path path;
//        try {
//            path = Paths.get(pathname);
//        } catch (Exception e) {
//            path = Paths.get(pathname.substring(1));
//        }
//        ;
//        try {
//            // 使用readAllLines的时候，小文件可以很快读取.
//            byte[] bytes = Files.readAllBytes(path);
//            String str = new String(bytes, "UTF-8");
//            System.out.println(str);
//            str = str.replaceAll("\r|\n|\\s", "");
//
//            return str;
//        } catch (IOException e) {
//            log.error("readConfigJson exception:{}", e);
//        }

        return null;
    }

    //private static final String CHANNEL_JSON = "[{\"channelId\":100,\"appId\":100,\"appName\":\"test\",\"input\":{\"type\":\"APP_LOG\",\"logPattern\":\"/home/work/log/tesla/server.log\",\"patternCode\":\"tesla_server_log\"},\"output\":{\"type\":\"rocketmq\",\"clusterInfo\":\"staging-cnbj2-rocketmq.namesrv.api.xiaomi.net:9876\",\"producerGroup\":\"RocketMQResStaging\",\"ak\":\"AK2RMWSENPXOP65KDB\",\"sk\":\"9GWdjoqRgZaN9EzoxGQDMguQncJ/AipR8Wq5SStH\",\"topic\":\"mi_log\"}}]";
}
