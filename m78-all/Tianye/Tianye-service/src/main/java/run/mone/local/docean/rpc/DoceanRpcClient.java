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

package run.mone.local.docean.rpc;

import com.alibaba.nacos.api.docean.NacosReq;
import com.alibaba.nacos.api.docean.NacosRes;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.context.TianyeContext;
import run.mone.local.docean.protobuf.*;
import run.mone.local.docean.rpc.processor.MessageProcessor;
import run.mone.local.docean.service.ZService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2022/4/17
 */
@Slf4j
@Data
@Component
public class DoceanRpcClient {

    private RpcClient client;

    private boolean init;

    private Gson gson = new Gson();

    @Value("$serverAddr")
    @Getter
    private String serverAddr;

    @Value("$userName")
    private String userName;

    @Value("$role")
    private String role;

    @Value("$alias")
    private String alias;

    @Value("$im.server")
    private String imServer;

    @Value("$public.token")
    private String publicToken;

    @Value("$nacos_config_server_addr")
    private String nacosServerAddr;

    @Resource
    private ZService zService;


    @PostConstruct
    public void init() {
        try {
            String userName = getUserName();
            if (StringUtils.isBlank(userName)) {
                log.warn("未获取到有效的用户名，请通过环境变量或者属性配置z token!!!");
                return;
            }
            Map<String, String> labels = new HashMap<>();
            labels.put("private", "true");
            Long id = zService.getKnowledgeIdByUserName(userName, labels);
            TianyeContext.ins().setUserName(userName);
            TianyeContext.ins().setKnowledgeBaseId(id);


            if (StringUtils.isEmpty(imServer)) {
                Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                    SafeRun.run(() -> {
                        imServer = getInstance();
                    });
                }, 5, 30, TimeUnit.SECONDS);
            }


            if (StringUtils.isBlank(serverAddr)) {
                log.warn("serverAddr is not set, try get latest addr every 30s!");
                if (client != null) {
                    client.shutdown();
                }
                String address = fetchNewServerAddress();
                log.info("fetchNewServerAddress rst:{}", address);
                serverAddr = address;
                initClient(address);

                Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                    SafeRun.run(() -> {
                        String newServerAddr = fetchNewServerAddress();
                        if (!newServerAddr.equals(serverAddr)) {
                            log.info("new server addr:{}", newServerAddr);
                            serverAddr = newServerAddr;
                            client.setServerAddrs(new AtomicReference<>(serverAddr));
                        }

                    });
                }, 5, 5, TimeUnit.SECONDS);
            } else {
                log.info("serverAddr is already set:{}, init client to connect", serverAddr);
                initClient(serverAddr);
            }
        } catch (Throwable ex) {
            log.error("init error:{}", ex.getMessage());
        }
    }

    @Value("$fetch_m78_addr")
    private String openMachineIpUrl;

    private String fetchNewServerAddress() {
        try {
            String url = openMachineIpUrl;
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            String machineRes = HttpClientV5.get(url, headers, 5000);
            JsonObject jsonObject = gson.fromJson(machineRes, JsonObject.class);
            String newServerAddr = jsonObject.get("data").getAsString();
            return newServerAddr;
        } catch (Throwable ex) {
            return "";
        }
    }

    private void initClient(String sAdr) {
        log.info("initClient sAdr:{},serverAddr:{}", sAdr, serverAddr);
        client = new RpcClient(sAdr);
        client.setReconnection(false);
        client.setTasks(Lists.newArrayList(new Task(() -> {
            log.info("ping m78server:" + serverAddr);
            PingMsg pingMsg = PingMsg.newBuilder().setUserName(TianyeContext.ins().getUserName()).setAlias(alias).setRole(role).build();
            client.sendMessage(serverAddr, TianyeCmd.pingReq, pingMsg.toByteArray(), 5000, true, cmd -> cmd.addExtField("protobuf", "true"));
        }, 5), new Task(() -> {
            log.info("ping imserver:" + imServer);
            PingMsg pingMsg = PingMsg.newBuilder().setUserName(TianyeContext.ins().getUserName()).setAlias(alias).setRole(role).build();
            client.sendMessage(imServer, TianyeCmd.pingReq, pingMsg.toByteArray(), 5000, true, cmd -> cmd.addExtField("protobuf", "true"));
        }, 5)));
        client.setProcessorList(Lists.newArrayList(new Pair(TianyeCmd.clientMessageReq, new MessageProcessor())));
        client.start(config -> config.setIdle(false));
        client.init();
        init = true;
        log.info("docean rpc client start finish");
    }

    public NacosRes req(String addr, NacosReq req) {
        return req(TianyeCmd.nacosReq, addr, req);
    }


    public NacosRes req(String addr, NacosReqProto req, Consumer<RemotingCommand> consumer) {
        return req(TianyeCmd.nacosReq, addr, req, consumer);
    }


    public ListResponse req(String addr, ListRequest req, Consumer<RemotingCommand> consumer) {
        try {
            log.debug("rpc req:{} {}", req.getCmd(), addr);
            RemotingCommand res = client.sendMessage(addr, TianyeCmd.listReq, req.toByteArray(), 5000, true, consumer);
            byte[] data = res.getBody();
            return ListResponse.parseFrom(data);
        } catch (Throwable ex) {
            log.error("rpc req error {}:{}:{}", addr, req.getCmd(), ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public NacosRes req(int code, String addr, NacosReqProto req, Consumer<RemotingCommand> consumer) {
        try {
            log.debug("rpc req:{} {}", req.getCmd(), addr);
            RemotingCommand res = client.sendMessage(addr, code, req.toByteArray(), 5000, true, consumer);
            byte[] data = res.getBody();
            NacosRes nacosRes = gson.fromJson(new String(data), NacosRes.class);
            return nacosRes;
        } catch (Throwable ex) {
            log.error("rpc req error {}:{}:{}", addr, req.getCmd(), ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * 直接获取 body 二进制(一般用protobuf压测,避免序列化反序列化的开销)
     *
     * @param code
     * @param addr
     * @param req
     * @param consumer
     * @return
     */
    public byte[] reqBytes(int code, String addr, NacosReqProto req, Consumer<RemotingCommand> consumer) {
        try {
            log.debug("rpc req:{} {}", req.getCmd(), addr);
            RemotingCommand res = client.sendMessage(addr, code, req.toByteArray(), 5000, true, consumer);
            return res.getBody();
        } catch (Throwable ex) {
            log.error("rpc req error {}:{}:{}", addr, req.getCmd(), ex.getMessage());
            throw new RuntimeException(ex);
        }
    }


    public NacosRes req(int code, String addr, NacosReq req) {
        try {
            log.debug("rpc req:{} {}", req.getCmd(), addr);
            RemotingCommand res = client.sendMessage(addr, code, gson.toJson(req), 5000, true);
            byte[] data = res.getBody();
            NacosRes nacosRes = gson.fromJson(new String(data), NacosRes.class);
            return nacosRes;
        } catch (Throwable ex) {
            log.error("rpc req error {}:{}:{}", addr, req.getCmd(), ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public AiResult req(int code, String addr, AiMessage req) {
        try {
            RemotingCommand res = client.sendMessage(addr, code, req.toByteArray(), 5000, true, cmd -> cmd.addExtField("protobuf", "true"));
            byte[] data = res.getBody();
            return AiResult.parseFrom(data);
        } catch (Throwable ex) {
            log.error("rpc req error {}:{}:{}", addr, req.getCmd(), ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * 不需要返回值
     *
     * @param addr
     * @param req
     */
    public boolean tell(String addr, NacosReq req) {
        try {
            log.debug("rpc tell:{} {}", addr, req.getCmd());
            client.tell(addr, TianyeCmd.distroReq, gson.toJson(req));
            return true;
        } catch (Throwable ex) {
            String message = "";
            if (req.isShowErrorMessage()) {
                message = ex.getMessage();
            }
            log.error("rpc tell error:{} {} {}", addr, req.getCmd(), message);
        }
        return false;
    }

    private String getUserName() {
        try {
            // HINT: 只允许通过token配置agent
            String token = retrieveToken();
            log.debug("User token is:{}", token);
            if (StringUtils.isNotBlank(token)) {
                TianyeContext.ins().setToken(token);
                String userByToken = zService.getUserByToken(token);
                log.debug("User token is:{}, userByToken:{}", token, userByToken);
                return userByToken;
            }
        } catch (Exception e) {
            log.error("Error while try to retrieve user name:", e);
            return "";
        }
        return "";
    }

    private String retrieveToken() {
        String token = this.publicToken;
        if (StringUtils.isEmpty(token)) {
            token = System.getProperty("token");
            if (StringUtils.isEmpty(token)) {
                token = System.getenv("tianye_token");
            }
        }
        return token;
    }

    private String getInstance() {
        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr(nacosServerAddr);
        nacosNaming.init();
        try {
            List<Instance> list = nacosNaming.getAllInstances("tianye-center");
            if (list != null && list.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (Instance instance : list) {
                    sb.append(instance.getIp()).append(":").append(instance.getPort()).append(",");
                }
                return sb.toString().substring(0, sb.length() - 1);
            } else {
                return "";
            }
        } catch (NacosException e) {
            log.error("Error while try to register instance", e);
            throw new RuntimeException(e);
        }
    }

}
