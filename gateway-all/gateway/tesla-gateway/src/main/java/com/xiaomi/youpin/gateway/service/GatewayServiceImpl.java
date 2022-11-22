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

package com.xiaomi.youpin.gateway.service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.common.Health;
import com.xiaomi.youpin.gateway.cache.ApiRouteCache;
import com.xiaomi.youpin.gateway.cache.TrafficRecordingCache;
import com.xiaomi.youpin.gateway.common.GateWayVersion;
import com.xiaomi.youpin.gateway.common.NetUtils;
import com.xiaomi.youpin.gateway.common.ScriptManager;
import com.xiaomi.youpin.gateway.common.Utils;
import com.xiaomi.youpin.gateway.context.GatewayServerContext;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.netty.filter.FilterManager;
import com.xiaomi.youpin.gateway.netty.filter.RequestFilterChain;
import com.xiaomi.youpin.gateway.plugin.TeslaPluginManager;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.qps.QpsAop;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfig;
import com.youpin.xiaomi.tesla.bo.*;
import com.youpin.xiaomi.tesla.service.TeslaGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Date;


/**
 * @author goodjava@qq.com
 * 对其他系统提供的服务
 * 使用租户id当group信息
 */
@DubboService(timeout = 3000, group = "${tenant.id}")
@Slf4j
public class GatewayServiceImpl implements TeslaGatewayService {

    @Autowired
    private ApiRouteCache cache;

    @Autowired
    private TrafficRecordingCache trafficRecordingCache;

    @Value("${plugin.path}")
    private String pluginPath;

    @Autowired
    private TeslaPluginManager teslaPluginManager;

    @Autowired
    private RequestFilterChain requestFilterChain;

    @Autowired
    private ConfigService configService;

    @Autowired
    private FilterManager filterManager;

    @Autowired
    private GatewayNamingService gatewayNamingService;

    @Autowired
    private QpsAop qpsAop;

    @Autowired
    private ScriptJarManager scriptJarManager;

    @Value("${dubbo.protocol.port}")
    private int dubboPort;


    @Autowired
    private GatewayServerContext gatewayServerContext;

    @Autowired
    private ScriptManager scriptManager;

    @Override
    public Result<Health> health() {
        long qps = qpsAop.getQps();
        return Result.success(new Health("0.0.1", "2020-03-28", qps));
    }


    @Override
    public Result<String> ping() {
        return Result.success("pong:" + System.currentTimeMillis());
    }

    @Override
    public Result<String> ping(Ping ping) {
        log.info("ping time:{}", ping.getTime());
        return Result.success("pong:" + ping.getData());
    }

    /**
     * dashboard 修改后能第一时间感知路由发生修改
     *
     * @param apiInfo
     * @return
     */
    @Override
    public Result<Boolean> updateApiInfo(ApiInfo apiInfo) {
        log.info("modify api tenant:{} info:{}", apiInfo.getTenant(), apiInfo.toString());
        if (apiInfo.getModifyType().equals(ModifyType.Delete)) {
            cache.delete(apiInfo);
            gatewayNamingService.unsubscribeWithPath(apiInfo.getPath(), apiInfo);
        } else if (apiInfo.getModifyType().equals(ModifyType.Add)) {
            cache.insert(apiInfo);
            gatewayNamingService.subscribeWithPath(apiInfo.getPath(), apiInfo);
        } else if (apiInfo.getModifyType().equals(ModifyType.Modify)) {
            gatewayNamingService.subscribeWithPath(apiInfo.getPath(), apiInfo);
            cache.modify(apiInfo);
        }
        return Result.success(true);
    }

    /**
     * 流量录制配置修改后能第一时间感知路由发生修改
     *
     * @param string
     * @return
     */
    @Override
    public Result<Boolean> updateRecordingTraffic(ModifyType opt, String string) {
        log.info("updateRecordingTraffic info:{}", string);
        if (opt == null) {
            return Result.success(true);
        }
        RecordingConfig recordingConfig = new Gson().fromJson(string, RecordingConfig.class);
        if (opt.equals(ModifyType.Delete)) {
            trafficRecordingCache.delete(recordingConfig);
        } else if (opt.equals(ModifyType.Add)) {
            trafficRecordingCache.insert(recordingConfig);
        } else if (opt.equals(ModifyType.Modify)) {
            trafficRecordingCache.modify(recordingConfig);
        }
        return Result.success(true);
    }


    /**
     * 启动插件(必须是停止状态的)
     *
     * @param plugInfo
     * @return
     */
    @Override
    public Result<Boolean> startPlugin(PlugInfo plugInfo) {
        log.info("startPlugin:{}", plugInfo);

        if (!Files.exists(Paths.get(pluginPath))) {
            try {
                Files.createDirectories(Paths.get(pluginPath));
            } catch (IOException e) {
                e.printStackTrace();
                return Result.fromException(e);
            }
        }

        Path path = Paths.get(pluginPath + File.separator + plugInfo.getFileName());
        try {
            Files.write(path, plugInfo.getData(), StandardOpenOption.CREATE_NEW);
            boolean res = teslaPluginManager.startPluginByName(plugInfo.getFileName(), plugInfo.getPluginId(), plugInfo.getDsIds());
            return Result.success(res);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Result.fromException(e);
        }
    }


    /**
     * 停止插件
     *
     * @param plugInfo
     * @return
     */
    @Override
    public Result<Boolean> stopPlugin(PlugInfo plugInfo) {
        try {
            Path path = Paths.get(pluginPath + File.separator + plugInfo.getFileName());
            if (Files.exists(path)) {
                log.info("stopPlugin:{}", plugInfo);
                teslaPluginManager.stopPlugin(plugInfo.getPluginId(), plugInfo.getUrl());
                Files.delete(path);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Result.fromException(e);
        }
        return Result.success(true);
    }

    @Override
    public Result<Boolean> reloadFilter() {
        requestFilterChain.reload("reload", Lists.newArrayList());
        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateFilter(String name, String groups, String type) {
        log.info("updateFilter:{} {} {}", name, groups, type);

        if (!needUpdate(name, groups, type)) {
            return Result.success(true);
        }

        log.info("{} {}", name, type);

        if (type.equals("add") || type.equals("remove")) {
            requestFilterChain.reload(type, Lists.newArrayList(name));
        }

        if (type.equals("remove")) {
            try {
                filterManager.releaseClassloader(name);
                Files.delete(Paths.get(Paths.get(configService.getSystemFilterPath()) + name));
            } catch (Throwable ex) {
                //ignore
            }
        }

        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateFilter(FilterParam param) {
        return this.updateFilter(param.getName(), param.getGroups(), param.getType());
    }

    /**
     * 如果不是这组网关的过滤器,则不需要更新
     *
     * @param name
     * @param groups
     * @param type
     * @return
     */
    private boolean needUpdate(String name, String groups, String type) {
        if (groups.equals(RequestFilter.DEFAULT_MACHINE_GROUP)) {
            return true;
        }
        String machineGroup = gatewayServerContext.getAgentName().get();
        return Arrays.stream(groups.split(",")).anyMatch(it -> it.equals(machineGroup));
    }

    @Override
    public Result<GatewayInfo> getGatewayInfo() {
        GatewayInfo gatewayInfo = new GatewayInfo();
        gatewayInfo.setIp(NetUtils.getLocalHost());
        gatewayInfo.setPid(Utils.ins().pid());
        gatewayInfo.setPort(dubboPort);
        gatewayInfo.setKey(gatewayInfo.getIp() + ":" + gatewayInfo.getPort());
        gatewayInfo.setUpdateTime(new Date());
        gatewayInfo.setVersion(new GateWayVersion().toString());

        //插件列表信息
        GatewayPluginInfoList gatewayPluginInfoList = gatewayServerContext.getPlugInfoList();
        gatewayInfo.setGatewayPluginInfoList(gatewayPluginInfoList);
        //获取filter信息
        gatewayInfo.setGatewayFilterInfoList(requestFilterChain.getFilterInfoList());

        return Result.success(gatewayInfo);
    }

    @Override
    public Result<Boolean> updateScript(Long id) {
        log.info("update script id:{}", id);
        scriptManager.removeScriptInfo(id);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> deployServerLessJar(String key, String jarUrl) {
        log.info("deploy server less jar:{} {}", key, jarUrl);
        scriptJarManager.loadJar(key, jarUrl);
        scriptManager.removeScriptInfo(Long.valueOf(key));
        return Result.success(true);
    }
}
