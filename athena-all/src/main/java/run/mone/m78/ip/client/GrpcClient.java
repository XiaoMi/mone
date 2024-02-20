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

package run.mone.m78.ip.client;

import com.google.gson.Gson;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import run.mone.m78.ip.util.HintUtils;
import run.mone.m78.ip.util.ProjectUtils;
import run.mone.mone.ultraman.grpc.UltramanRequest;
import run.mone.mone.ultraman.grpc.UltramanResponse;
import run.mone.mone.ultraman.grpc.UltramanServiceGrpc;
import run.mone.m78.ip.action.ActionEnum;
import run.mone.m78.ip.bo.TeslaPluginConfig;
import run.mone.m78.ip.common.NotificationCenter;
import run.mone.m78.ip.common.PluginVersion;
import run.mone.m78.ip.common.UltramanNotifier;
import run.mone.m78.ip.service.DocumentService;
import run.mone.m78.ip.service.ImageService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang.mutable.MutableObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/5 10:42
 */
public class GrpcClient {

    private ManagedChannel channel;

    private TeslaPluginConfig config;

    private String ip;

    private boolean close = true;

    public void init() {
        if (close) {
            return;
        }

        String v = PropertiesComponent.getInstance().getValue("tesla_plugin_token");
        if (null != v) {
            try {
                config = new Gson().fromJson(v, TeslaPluginConfig.class);
                if (!StringUtils.equals(config.getChatServer(), ip)) {
                    try {
                        channel.shutdownNow();
                    } catch (Throwable ex) {
                    }
                }

                ip = config.getChatServer();
                System.out.println("Ultraman grpc channel build");
                channel = ManagedChannelBuilder.forAddress(ip, 5555).usePlaintext().build();

                new Thread(() -> {
                    int i = 0;
                    MessageBusConnection connection = null;
                    for (; ; ) {
                        System.out.println("connect grpc num:" + (i++));
                        CountDownLatch latch = new CountDownLatch(1);
                        try {
                            UltramanResponse res = call(UltramanRequest.newBuilder().setCmd("version").build());
                            if (null != res && (Integer.valueOf(res.getData()) > Integer.valueOf(new PluginVersion().toString()))) {
                                //需要升级
                                ApplicationManager.getApplication().invokeLater(() -> {
                                    NotificationCenter.notice("Ultraman Plugin Update");
                                });
                            }
                            StreamObserver<UltramanRequest> streamObserver = this.listener(latch);
                            MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
                            connection = messageBus.connect();
                            connection.subscribe(UltramanNotifier.ULTRAMAN_ACTION_TOPIC, event -> {
                                System.out.println(event.getCmd());
                                switch (event.getCmd()) {
                                    case "save_config": {
                                        break;
                                    }
                                    case "req": {
                                        streamObserver.onNext((UltramanRequest) event.getData());
                                        break;
                                    }
                                }
                            });
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                            latch.countDown();
                        }
                        try {
                            latch.await();
                            TimeUnit.SECONDS.sleep(3);
                        } catch (Throwable ex) {

                        } finally {
                            if (null != connection) {
                                connection.disconnect();
                            }
                        }
                    }
                }).start();
            } catch (Throwable ex) {
                System.out.println("Ultraman error:" + ex.getMessage());
            }
        } else {
            ApplicationManager.getApplication().invokeLater(() -> {
                Messages.showMessageDialog("请初始化Ultraman配置(user)", "message", Messages.getInformationIcon());
            });
        }
    }

    public UltramanResponse call(UltramanRequest req) {
        UltramanServiceGrpc.UltramanServiceBlockingStub stub2 = UltramanServiceGrpc.newBlockingStub(channel);
        UltramanResponse res = stub2.hello(req);
        return res;
    }

    public StreamObserver<UltramanRequest> listener(CountDownLatch latch) {
        UltramanServiceGrpc.UltramanServiceStub stub = UltramanServiceGrpc.newStub(channel);
        MutableObject mo = new MutableObject();
        final StreamObserver<UltramanRequest> reqStream = stub.stream(new StreamObserver<UltramanResponse>() {
            @Override
            public void onNext(UltramanResponse res) {
                System.out.println(res.getData());
                //通知
                if (res.getCmd().equals("notify")) {
                    String data = res.getData();
                    if (!res.getResMapOrDefault("user", "").equals("")) {
                        data = res.getResMapOrDefault("user", "") + ":" + data;
                    }
                    String type = res.getResMapOrDefault("type", "0");
                    if (type.equals("0")) {
                        NotificationCenter.notice(data);
                        HintUtils.show(null, data);
                    } else {
                        HintUtils.show(null, data);
                    }
                }
                //查看图片
                if (res.getCmd().equals("image")) {
                    new ImageService().open(ActionEnum.zero.name());
                }
                //显示代码
                if (res.getCmd().equals("show_code")) {
                    MutableObject moc = new MutableObject();
                    ApplicationManager.getApplication().invokeAndWait(() -> {
                        int v = Messages.showOkCancelDialog(ProjectUtils.project(), "有人给你推送了代码,是否选择查看", "选择", Messages.getInformationIcon());
                        moc.setValue(v);
                    });
                    if ((int) moc.getValue() == 0) {
                        new DocumentService().open(res.getResMapMap().get("fileName"), res.getData());
                    }
                }
                //查看代码(别人主动要看代码,返回我的代码)
                if (res.getCmd().equals("begin_review")) {
                    String user = res.getResMapMap().get("review");

                    MutableObject moc = new MutableObject();
                    ApplicationManager.getApplication().invokeAndWait(() -> {
                        int v = Messages.showOkCancelDialog(ProjectUtils.project(), user + "想要查看你当前的code", "选择", Messages.getInformationIcon());
                        moc.setValue(v);
                    });

                    if ((int) moc.getValue() != 0) {
                        return;
                    }

                    Pair<String, String> data = new DocumentService().getContent(null);
                    if (null == data) {
                        data = Pair.of("对方没有打开源码", "tmp.java");
                    }
                    StreamObserver<UltramanRequest> reqStream = (StreamObserver<UltramanRequest>) mo.getValue();
                    UltramanRequest req = UltramanRequest.newBuilder().setCmd("begin_review_res")
                            .setParams(data.getKey())
                            .putParamMap("fileName", data.getValue())
                            .putParamMap("review", user)
                            .putParamMap("user", res.getResMapMap().get("user"))
                            .build();
                    reqStream.onNext(req);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });
        mo.setValue(reqStream);
        UltramanRequest loginReq = UltramanRequest.newBuilder().setCmd("login").putParamMap("user", config.getNickName()).build();
        reqStream.onNext(loginReq);
        return reqStream;

    }

}
