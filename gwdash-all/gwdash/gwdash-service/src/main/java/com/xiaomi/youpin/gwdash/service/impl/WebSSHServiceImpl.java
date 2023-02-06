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

package com.xiaomi.youpin.gwdash.service.impl;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.xiaomi.youpin.gwdash.agent.AgentManager;
import com.xiaomi.youpin.gwdash.bo.SSHConnectInfo;
import com.xiaomi.youpin.gwdash.bo.WebSSHData;
import com.xiaomi.youpin.gwdash.common.ConstantPool;
import com.xiaomi.youpin.gwdash.service.RootKeyManager;
import com.xiaomi.youpin.gwdash.service.UserService;
import com.xiaomi.youpin.gwdash.service.WebSSHService;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
import com.xiaomi.youpin.sre.ssh.Ssh;
import com.xiaomi.youpin.tesla.agent.po.ShellReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @Author: zhangjunyi
 * @Date: 2020/3/8
 */
@Service
@Slf4j
public class WebSSHServiceImpl implements WebSSHService {

    private static Map<String, Object> sshMap = new ConcurrentHashMap<>();


    private static final String clear = "__clear__";

    private static final String init = "__init__";

    private ExecutorService executorService = new ThreadPoolExecutor(30, 30,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());


    @Value("${ssh.key.path}")
    private String sshKeyPath;

    @Autowired
    private UserService userService;

    @Value("${hermes.project.name}")
    private String projectName;

    @Autowired
    private RootKeyManager rootKeyManager;


    @Autowired
    private AgentManager agentManager;

    /**
     * @Description: 初始化连接
     * @Param: [session]
     * @return: void
     * @Author: zhangjunyi
     * @Date: 2020/4/7
     */
    @Override
    public void initConnection(WebSocketSession session) {
        SSHConnectInfo sshConnectInfo = new SSHConnectInfo();
        sshConnectInfo.setWebSocketSession(session);
        String uid = String.valueOf(session.getAttributes().get("username"));
        sshConnectInfo.setName(uid);
        log.info("init connection session id:{} {}", session.getId(), uid);
        sshMap.putIfAbsent(session.getId(), sshConnectInfo);
    }

    /**
     * @Description: 处理客户端发送的数据
     */
    @Override
    public void recvHandle(String buffer, WebSocketSession session) {
        WebSSHData webSSHData = new Gson().fromJson(buffer, WebSSHData.class);
        webSSHData.setPort(22);
        if (ConstantPool.WEBSSH_OPERATE_CONNECT.equals(webSSHData.getOperate())) {
            String ipAndPort = webSSHData.getHost() + ":" + webSSHData.getAgentPort();
            session.getAttributes().put("host", ipAndPort);
            plantSshKey(ipAndPort);

            SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(session.getId());
            WebSSHData finalWebSSHData = webSSHData;
            executorService.execute(() -> {
                try {
                    connectToSSH(sshConnectInfo, finalWebSSHData, session, false);
                } catch (JSchException | IOException e) {
                    log.error("error:" + e.getMessage(), e);
                    try {
                        this.sendMessage(session,e.getMessage().getBytes());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    close(session);
                }
            });
        } else if (ConstantPool.WEBSSH_OPERATE_COMMAND.equals(webSSHData.getOperate())) {
            String command = webSSHData.getCommand();
            SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(session.getId());
            if (sshConnectInfo != null) {
                try {
                    transToSSH(sshConnectInfo.getChannel(), command);
                } catch (IOException e) {
                    log.error("error:{}", e.getMessage());
                    close(session);
                }
            }
        } else {
            log.error("don't support");
            close(session);
        }
    }

    @Override
    public void recvAgentLogHanlde(String buffer, WebSocketSession session) {
        WebSSHData webSSHData = new Gson().fromJson(buffer, WebSSHData.class);
        webSSHData.setPort(22);
        // 下面代码本地测试使用 用于连接本地的ssh
      //  webSSHData.setHost("");
      //  webSSHData.setUsername("zhangjunyi");
        if (ConstantPool.WEBSSH_OPERATE_CONNECT.equals(webSSHData.getOperate())) {
            SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(session.getId());
            WebSSHData finalWebSSHData = webSSHData;
            String ipAndPort = webSSHData.getHost() + ":" + webSSHData.getAgentPort();
            session.getAttributes().put("host", ipAndPort);
            plantSshKey(ipAndPort);
            executorService.execute(() -> {
                try {
                    connectToSSH(sshConnectInfo, finalWebSSHData, session, true);
                } catch (JSchException | IOException e) {
                    log.error("error:" + e.getMessage(), e);
                    close(session);
                }
            });
        } else {
            log.error("don't support");
            close(session);
        }
    }

    @Override
    public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
        session.sendMessage(new TextMessage(buffer));
    }

    @Override
    public void close(WebSocketSession session) {
        SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(session.getId());
        if (sshConnectInfo != null) {
            if (sshConnectInfo.getChannel() != null) {
                sshConnectInfo.getChannel().disconnect();
            }
            sshMap.remove(session.getId());
            removeSshKey((String) session.getAttributes().get("host"));
        }
    }

    @Override
    public void plantSshKey(String address) {
        log.info("plantSshKey ip:{}", address);
        if (rootKeyManager.plant(address)) {
            ShellReq req = new ShellReq();
            req.setShellCmd(init);
            req.setPath("/root/");
            agentManager.send(address, 5000, new Gson().toJson(req), 5000);
        }
    }

    @Override
    public void removeSshKey(String address) {
        log.info("removeSshKey ip:{}", address);
        ShellReq req = new ShellReq();
        req.setShellCmd(clear);
        req.setPath("/root/");
        agentManager.send(address, 5000, new Gson().toJson(req), 5000);
        rootKeyManager.remove(address);
    }

    /**
     * 使用jsch连接终端
     *
     * @param sshConnectInfo
     * @param webSSHData
     * @param webSocketSession
     * @param islog            是否是日志接口调用
     * @throws JSchException
     * @throws IOException
     */
    private void connectToSSH(SSHConnectInfo sshConnectInfo, WebSSHData webSSHData, WebSocketSession webSocketSession, Boolean islog) throws JSchException, IOException {
        Ssh ssh = new Ssh();
        ssh.connect(webSSHData.getUsername(), webSSHData.getHost(), webSSHData.getPort(), sshKeyPath);
        sshConnectInfo.setChannel(ssh.getChannel());
        Boolean isAdmin = isAdmin(sshConnectInfo);

        if (islog) {
            if(!webSSHData.getLogPath().startsWith("xxxx/log/")){
                this.sendMessage(webSocketSession, "只能查看xxxx/log/下的文件".getBytes());
            }else{
                transToSSH(ssh.getChannel(), "tail -f " + webSSHData.getLogPath() + "\r");
            }
        } else {
            if (!isAdmin) {
                //切换为rd账户
                transToSSH(ssh.getChannel(), "su rd\r");
                transToSSH(ssh.getChannel(), "cd /tmp/\r");
            } else {
                transToSSH(ssh.getChannel(), "\r");
            }
        }

        InputStream inputStream = ssh.getChannel().getInputStream();
        try {
            byte[] buffer = new byte[1024];
            int i = 0;
            while ((i = inputStream.read(buffer)) != -1) {
                sendMessage(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
            }
        } catch (Throwable ex) {
            log.error("ssh error:" + ex.getMessage(), ex);
        } finally {
            log.info("ssh close");
            ssh.close();
            if (inputStream != null) {
                inputStream.close();
            }
        }

    }

    /**
     * 将消息转发到终端
     */
    private void transToSSH(Channel channel, String command) throws IOException {
        if (channel != null) {
            OutputStream outputStream = channel.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
        }
    }

    /**
     * isAdmin
     */
    private Boolean isAdmin(SSHConnectInfo sshConnectInfo) {
        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
        queryRoleRequest.setProjectName(projectName);
        queryRoleRequest.setUserName(sshConnectInfo.getName());
        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
        List<String> roleNames = roles.stream().map(it -> it.getName()).collect(Collectors.toList());
        return roleNames.contains("admin");
    }
}