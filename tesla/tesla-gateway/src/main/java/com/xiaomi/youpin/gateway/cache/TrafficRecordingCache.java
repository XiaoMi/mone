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

package com.xiaomi.youpin.gateway.cache;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.gateway.common.Utils;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.RecordingStatusEnum;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.GetRecordingConfigListReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfig;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfigList;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.Result;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.GatewayEnvTypeEnum;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.RecordingSourceTypeEnum;
import com.xiaomi.youpin.tesla.traffic.recording.api.service.RecordingDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author dingpei
 */
@Component
@Slf4j
public class TrafficRecordingCache {

    public static final String DEFAULR_ALL_API = "*";

    /**
     *只缓存录制中的配置，待录制的不缓存
     */
    private static final ConcurrentHashMap<String, RecordingConfig> cache = new ConcurrentHashMap<>();

    private static final AtomicBoolean haveCache = new AtomicBoolean(false);

    @Value("${cache.route.path}")
    private String cacheRoutePath;

    private static final String fileName = "recording_traffic_route.cache";

    @Reference(check = false, interfaceClass = RecordingDubboService.class, group = "${recording.dubbo.group}")
    private RecordingDubboService recordingDubboService;

    private int pageSize = 300;

    @Value("${env.group}")
    private String envGroup;


    @Autowired
    private ConfigService configService;



    @PostConstruct
    public void init() {
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
            try {

                if (!configService.isOpenTrafficRecord()) {
                    return;
                }

                List<RecordingConfig> list = Lists.newArrayList();
                int pageNum = 1;
                GetRecordingConfigListReq req = getRecordingConfigListReq();
                while (true) {
                    //只获取录制中的录制配置，同一个url允许配多个录制配置，但同一个url只允许一个配置在录制中
                    req.setPage(pageNum);
                    Result<RecordingConfigList> res = recordingDubboService.getRecordingConfigList(req);
                    log.debug("RecordingTrafficCache.getRecordingConfig, res code: {}", res.getCode());
                    if (res.getCode() == 0) {
                        res.getData().getList().stream().filter(it1 -> it1.getStatus() == RecordingStatusEnum.RECORDING.getCode()).forEach(it -> {
                            cache.put(it.getGatewaySource().getUrl(), it);
                        });
                        list.addAll(res.getData().getList().stream().filter(it1 -> it1.getStatus() == RecordingStatusEnum.RECORDING.getCode()).collect(Collectors.toList()));
                        if (res.getData().getList().size() == 0) {
                            break;
                        }
                        pageNum++;
                    } else {
                        log.error("failed to get RecordingConfigList, res: {}", res);
                        break;
                    }
                }
                log.info("RecordingConfigList update size:{}", list.size());
                Utils.writeFile(cacheRoutePath, fileName, new Gson().toJson(list));
                haveCache.compareAndSet(false, true);
            } catch (Exception ex) {
                log.error("RecordingTrafficCache.init, " + ex.getMessage(), ex);
                try {
                    if (haveCache.get() == false) {
                        String data = Utils.readFile(cacheRoutePath, fileName);
                        Type type = new TypeToken<List<RecordingConfig>>() {
                        }.getType();
                        List<RecordingConfig> recordingConfigs = new Gson().fromJson(data, type);
                        if (recordingConfigs != null) {
                            recordingConfigs.stream().forEach(it -> {
                                if (it.getStatus() == RecordingStatusEnum.RECORDING.getCode()) {
                                    cache.put(it.getGatewaySource().getUrl(), it);
                                }
                            });
                            haveCache.compareAndSet(false, true);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

    private GetRecordingConfigListReq getRecordingConfigListReq() {
        GetRecordingConfigListReq req = new GetRecordingConfigListReq();
        req.setPageSize(pageSize);
        req.setSourceType(RecordingSourceTypeEnum.GATEWAY.getCode());
        req.setStatus(RecordingStatusEnum.RECORDING.getCode());
        if (envGroup == null) {
            return req;
        }
        switch (envGroup) {
            case "online": {
                req.setEnvType(GatewayEnvTypeEnum.ONLINE.getCode());
                return req;
            }
            case "staging": {
                req.setEnvType(GatewayEnvTypeEnum.STAGING.getCode());
                return req;
            }
            case "intranet": {
                req.setEnvType(GatewayEnvTypeEnum.INTRANET.getCode());
                return req;
            }
        }
        return req;
    }

    /**
     * 新增录制配置
     */
    public void insert(RecordingConfig recordingConfig) {
        if (recordingConfig.getStatus() != RecordingStatusEnum.RECORDING.getCode()) {
            return;
        }
        cache.put(recordingConfig.getGatewaySource().getUrl(), recordingConfig);
    }

    /**
     * 删除录制配置
     */
    public void delete(RecordingConfig recordingConfig) {
        cache.remove(recordingConfig.getGatewaySource().getUrl());
    }

    /**
     * 修改录制配置
     */
    public void modify(RecordingConfig recordingConfig) {
        Optional<String> optional = cache.entrySet().stream().filter(it -> it.getValue().getId() == recordingConfig.getId()).map(it -> it.getKey()).findFirst();
        if (optional.isPresent()) {
            cache.remove(optional.get());
        }
        if (recordingConfig.getStatus() != RecordingStatusEnum.RECORDING.getCode()) {
            return;
        }
        cache.put(recordingConfig.getGatewaySource().getUrl(), recordingConfig);
    }

    /**
     * 获取录制配置
     *
     * @param url
     * @return
     */
    public RecordingConfig get(String url) {
        return cache.get(url);
    }
}
