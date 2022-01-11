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

package com.xiaomi.mone.ssh;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;

/**
 * 　@description: SSH server
 * 　@author zhenghao
 *
 */
public class SimpleSshServer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static SshServer sshServer = null;

    /**
     * 创建可以重复调用，但不会重复执行
     * @param port
     * @param u
     * @param p
     * @return
     * @throws IOException
     */
    public int beginSSH(String host, int port, final String u, final String p) {
        // SSH服务
        if (sshServer == null) {
            sshServer = SshServer.setUpDefaultServer();
        } else {
            return ConstsSSH.CREATED;
        }
        // 禁止 bouncy castle，避免版本冲突
        System.setProperty("org.apache.sshd.registerBouncyCastle", "true");
        // 指定提供ssh服务的IP
        sshServer.setHost(host);
        // 指定ssh服务的端口
        sshServer.setPort(port);
        // 指定密码认证
        sshServer.setPasswordAuthenticator(
                new PasswordAuthenticator() {
                    @Override
                    public boolean authenticate(String username, String password,
                                                ServerSession session) {
                        return u.equals(username) && p.equals(password);
                    }
                }
        );
        sshServer.setShellFactory(InteractiveProcessShellFactory.INSTANCE);
        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshServer.setCommandFactory(new ScpCommandFactory());
        sshServer.setCommandFactory(new CommandFactory() {
            @Override
            public Command createCommand(String command) {
                logger.info("command:{}", command);
                return new ProcessShellFactory(command.split(";")).create();
            }
        });

        try {
            sshServer.start();
        } catch (IOException e) {
            logger.error("SimpleSshServer beginSSH error", e);
            this.stopSSH();
            return ConstsSSH.CREATED_ERROR;
        }
        return ConstsSSH.SUCCESS;
    }

    /**
     * windows下的Shell
     *
     * @return
     */
    public static ProcessShellFactory getShellFactory4Win(){
        return new ProcessShellFactory(new String[] { "cmd.exe" }, EnumSet.of(
                ProcessShellFactory.TtyOptions.Echo,
                ProcessShellFactory.TtyOptions.ICrNl,
                ProcessShellFactory.TtyOptions.ONlCr));
    }

    /**
     * unix下的Shell
     *
     * @return
     */
    public static ProcessShellFactory getShellFactory4Unix(){
        return new ProcessShellFactory(new String[] { "/bin/sh", "-i", "-l" }, EnumSet
                .of(ProcessShellFactory.TtyOptions.ONlCr));
    }


    /**
     * 停止可以重复调用但不一定会成功
     * @return
     */
    public int stopSSH() {
        try {
            sshServer.stop();
            return ConstsSSH.SUCCESS;
        } catch (IOException e) {
            logger.error("SimpleSshServer stopSSH error", e);
            sshServer = null;
            return ConstsSSH.STOP_ERROR;
        }
    }
}
