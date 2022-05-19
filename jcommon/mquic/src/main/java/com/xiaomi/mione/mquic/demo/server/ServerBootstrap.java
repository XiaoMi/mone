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
