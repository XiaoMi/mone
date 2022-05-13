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

package com.xiaomi.mione.mquic.demo.server;

import com.xiaomi.mione.mquic.demo.server.dispatcher.Dispatcher;
import com.xiaomi.mione.mquic.demo.server.manager.ChannelManager;
import com.xiaomi.mione.mquic.demo.server.manager.TaskManager;

import java.security.cert.CertificateException;

/**
 * @author goodjava@qq.com
 * @date 9/5/21
 */
public class ServerBootstrap {


    public static void main(String[] args) throws CertificateException, InterruptedException {
        Dispatcher dispatcher = new Dispatcher();
        ChannelManager channelManager = new ChannelManager();
        TaskManager taskManager = new TaskManager();
        taskManager.setChannelManager(channelManager);
        taskManager.execute();
        QuicServer quicServer = new QuicServer();
        quicServer.setDispatcher(dispatcher);
        quicServer.setChannelManager(channelManager);
        quicServer.start();
    }

}
