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

package com.xiaomi.youpin.tesla.ip.component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.Consumer;
import com.xiaomi.youpin.tesla.client.ws.WsClient;
import com.xiaomi.youpin.tesla.ip.bo.Response;
import com.xiaomi.youpin.tesla.ip.bo.UserVo;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.MessageQueue;
import com.xiaomi.youpin.tesla.ip.common.OpenImageConsumer;
import com.xiaomi.youpin.tesla.ip.common.OpenTextConsumer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class TeslaAppComponent implements ApplicationComponent {

    private static final Logger logger = Logger.getInstance(OpenImageConsumer.class);

    @Override
    public void initComponent() {
        logger.info("tesla app component init:0.0.1:2019-06-01");
        String chatServer = ConfigUtils.getConfig().getChatServer();
        WsClient client = new WsClient();
        //处理接收到的信息
        new Thread(() -> {
            try {
                if (StringUtils.isEmpty(chatServer)) {
                    return;
                }

                client.init(new URI(chatServer), messageConsumer(), () -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("uri", "/mtop/arch/im");
                    m.put("cmd", "ping");
                    m.put("data", "ping");
                    return new Gson().toJson(m);
                });

                //具备断线重连能力
                client.connect();
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            }

        }).start();


        new Thread(() -> {
            while (true) {
                Map<String, Object> message = MessageQueue.ins().pollSend();
                if (null != message) {
                    Map<String, Object> m = new HashMap<>(10);
                    m.put("uri", "/mtop/arch/im");
                    m.putAll(message);
                    client.send(new Gson().toJson(m));
                }
            }
        }).start();

    }

    @NotNull
    private java.util.function.Consumer<String> messageConsumer() {
        return msg -> {

            logger.info("message consumer msg:" + msg);
            final Map<String, Object> m = new Gson().fromJson(msg, Map.class);

            if (null == m || null == m.get("cmd")) {
                logger.warn("msg:" + m);
                return;
            }

            try {
                //收到通知
                if (m.get("cmd").toString().equals("notify")) {
                    String data = m.get("data").toString();
                    logger.info("notify:" + data);

                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showMessageDialog(data, "notify", Messages.getInformationIcon());
                    });
                    return;
                }

                if (m.get("cmd").toString().equals("login")) {
                    Response<List<UserVo>> response = new Gson().fromJson(msg, new TypeToken<Response<List<UserVo>>>() {
                    }.getType());
                    MessageQueue.ins().offer(response);
                    return;
                }

                //有人加入聊天室
                if (m.get("cmd").toString().toUpperCase().equals("login_user_info")) {
                    Response<UserVo> response = new Gson().fromJson(msg, new TypeToken<Response<UserVo>>() {
                    }.getType());
                    MessageQueue.ins().offer(response);
                    return;
                }

                if (m.get("cmd").toString().equals("talk_message")) {
                    talk(msg);
                    return;
                }

                //打开推广
                if (m.get("cmd").toString().equals("ad")) {
                    openAd(m);
                    return;
                }

                //打开文本
                if (m.get("cmd").toString().equals("review")) {
                    openText(m);
                    return;
                }

                if (m.get("cmd").toString().equals("logout_msg")) {
                    Response<String> response = new Gson().fromJson(msg, new TypeToken<Response<String>>() {
                    }.getType());
                    MessageQueue.ins().offer(response);
                    return;
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        };
    }

    private void talk(String msg) {
        try {
            Response<String> response = new Gson().fromJson(msg, new TypeToken<Response<String>>() {
            }.getType());
            MessageQueue.ins().offer(response);
        } catch (Exception ex) {
            System.out.println("talk ex:" + ex.getMessage());
        }
    }

    /**
     * 打开广告
     *
     * @param m
     */
    private void openAd(Map<String, Object> m) {
        ApplicationManager.getApplication().invokeLater(() -> {

            DataManager.getInstance().getDataContextFromFocus()
                    .doWhenDone((Consumer<DataContext>) (dataContext -> new OpenImageConsumer(m.get("data").toString()).accept(dataContext)))
                    .doWhenRejected((Consumer<String>) logger::error);

        });
    }


    /**
     * 打开文本
     */
    private void openText(Map<String, Object> m) {
        List<String> list = (List<String>) m.get("data");

        ApplicationManager.getApplication().invokeLater(() -> {

            DataManager.getInstance().getDataContextFromFocus()
                    .doWhenDone((Consumer<DataContext>) (dataContext ->
                            new OpenTextConsumer(list.get(0), list.get(1)).accept(dataContext)))
                    .doWhenRejected((Consumer<String>) logger::error);

        });
    }
}
