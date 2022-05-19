package com.xiaomi.youpin.sre.ssh;

import com.jcraft.jsch.*;
import lombok.Data;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Data
public class Ssh {


    private Session session;

    private Channel channel;


    public static void main(String... args) throws JSchException, IOException {
        new Ssh().connect("user", "127.0.0.1", "/tmp/ssh/id_rsa", System.in, System.out);
    }


    public void connect(String name, String host, String keyPath, InputStream in, OutputStream out) throws JSchException {
        JSch jsch = new JSch();
        jsch.addIdentity(keyPath);

        Session session = jsch.getSession(name, host, 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(10000);

        System.out.println(session.isConnected());

        Channel channel = session.openChannel("shell");
        channel.setInputStream(in);
        channel.setOutputStream(out);

        channel.connect();
    }


    public void connect(String name, String host, int port, String keyPath) throws JSchException {
        JSch jsch = new JSch();
        jsch.addIdentity(keyPath);
        session = jsch.getSession(name, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(10000);
        System.out.println(session.isConnected());
        channel = session.openChannel("shell");
        ChannelShell channelShell = (ChannelShell)channel;
        channelShell.setPtySize(111,33,980,550);
        channelShell.connect();

    }


    public void close() {
        if (null != session) {
            session.disconnect();
        }
        if (channel != null) {
            channel.disconnect();
        }
    }

    public String cmd(String cmd, String name, String host, String keyPath) throws JSchException, IOException {
        JSch jsch = new JSch();
        jsch.addIdentity(keyPath);

        Session session = jsch.getSession(name, host, 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(10000);

        ChannelExec channel = (ChannelExec) session.openChannel("exec");

        channel.setCommand(cmd);

        channel.connect();

        InputStream in = channel.getInputStream();

        StringBuffer buf = new StringBuffer(1024);
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                buf.append(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                int res = channel.getExitStatus();
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        channel.disconnect();
        return buf.toString();
    }


}
