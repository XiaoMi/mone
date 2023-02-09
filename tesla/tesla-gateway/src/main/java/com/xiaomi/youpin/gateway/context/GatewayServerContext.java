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

package com.xiaomi.youpin.gateway.context;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.youpin.gateway.common.NetUtils;
import com.xiaomi.youpin.gateway.netty.filter.RequestFilterChain;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.xiaomi.youpin.qps.QpsAop;
import com.youpin.xiaomi.tesla.bo.GatewayInfo;
import com.youpin.xiaomi.tesla.bo.GatewayPluginInfo;
import com.youpin.xiaomi.tesla.bo.GatewayPluginInfoList;
import com.youpin.xiaomi.tesla.bo.ServerInfo;
import com.youpin.xiaomi.tesla.service.TeslaOpsService;
import com.xiaomi.youpin.gateway.common.Utils;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import com.xiaomi.youpin.gateway.plugin.TeslaPluginManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class GatewayServerContext {

    private static final Logger logger = LoggerFactory.getLogger(GatewayServerContext.class);

    @Value("${dubbo.protocol.port}")
    private int dubboPort;

    @Reference(check = false, group = "${dubbo.group}")
    private TeslaOpsService teslaOpsService;

    public static final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Autowired
    private TeslaPluginManager teslaPluginManager;


    @Autowired
    private RequestFilterChain requestFilterChain;


    @Autowired
    private QpsAop qpsAop;

    /**
     * 存活的agent数量
     */
    private AtomicInteger agentNum = new AtomicInteger(1);

    private AtomicReference<String> agentGroup = new AtomicReference("");

    private AtomicBoolean firstLoad = new AtomicBoolean(true);


    @PostConstruct
    public void init() {
        //同步信息到ops服务器
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            logger.info("GatewayServerContext schedule");
            try {
                GatewayInfo gatewayInfo = new GatewayInfo();
                gatewayInfo.setIp(NetUtils.getLocalHost());
                gatewayInfo.setPid(Utils.ins().pid());
                gatewayInfo.setPort(dubboPort);
                gatewayInfo.setGroup(agentGroup.get());
                gatewayInfo.setKey(gatewayInfo.getIp() + ":" + gatewayInfo.getPort());
                gatewayInfo.setUpdateTime(new Date());

                log.info("update gateway info:{}", new Gson().toJson(gatewayInfo));

                //插件列表信息
                GatewayPluginInfoList gatewayPluginInfoList = getPlugInfoList();
                gatewayInfo.setGatewayPluginInfoList(gatewayPluginInfoList);

                Result<ServerInfo> info = teslaOpsService.updateGatewayInfo(gatewayInfo);
                if (info.getCode() == GeneralCodes.OK.getCode()) {
                    agentNum.set(info.getData().getAgentNum());

                    String oldGroup = agentGroup.get();
                    String newGroup = info.getData().getGroup();
                    agentGroup.set(newGroup);

                    //group 发生了变更,需要从新拉取filter,因为filter每个组不同
                    if (newGroup != null && !newGroup.equals(oldGroup) && !firstLoad.get()) {
                        requestFilterChain.reload("init", Lists.newArrayList());
                    }
                    firstLoad.set(false);

                    logger.info("agent group:{} num:{} qps:{}", info.getData().getGroup(), agentNum.get(), this.qpsAop.getQps());
                }
            } catch (Throwable ex) {
                logger.error(ex.getMessage());
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    public GatewayPluginInfoList getPlugInfoList() {
        try {
            GatewayPluginInfoList gatewayPluginInfoList = new GatewayPluginInfoList();
            List<String> list = teslaPluginManager.pluginList();

            List<GatewayPluginInfo> infos = list.stream().map(it -> {
                GatewayPluginInfo info = new GatewayPluginInfo();
                info.setName(it);
                return info;
            }).collect(Collectors.toList());

            gatewayPluginInfoList.getList().addAll(infos);
            return gatewayPluginInfoList;
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            return new GatewayPluginInfoList();
        }
    }


    public AtomicInteger getAgentNum() {
        return agentNum;
    }

    public AtomicReference<String> getAgentName() {
        return agentGroup;
    }
}
