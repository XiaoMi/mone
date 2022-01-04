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

package com.xiaomi.youpin.tesla.agent.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.management.OperatingSystemMXBean;
import com.xiaomi.youpin.tesla.agent.common.Config;
import com.xiaomi.youpin.tesla.agent.common.Safe;
import com.xiaomi.youpin.tesla.agent.po.DeployInfo;
import com.xiaomi.youpin.tesla.agent.po.SreLabel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class DeployService {


    private static final String cacheFileName = "mione_agent.cache.a";

    private static final String cacheFilePath = Config.ins().get("cache_path", "xxxx/");

    private static final String config = "__config__";

    private static final String container_config = "__container__config__";

    public static final int DEFAULT_PORT_NUM = 6500;

    public int cpuNum;

    public long mem;

    public AtomicInteger portNum = new AtomicInteger(DEFAULT_PORT_NUM);

    @Getter
    private SreLabel sreLabel;

    private DeployService() {
        cpuNum = getCpuNum();
        mem = getMem();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            loadSreLabel();
            save();
        }, 10, 30, TimeUnit.SECONDS);
    }

    private static class LazyHolder {
        private static DeployService ins = new DeployService();
    }

    public static final DeployService ins() {
        return LazyHolder.ins;
    }


    private ConcurrentHashMap<String, DeployInfo> map = new ConcurrentHashMap<>(30);


    public void createOrUpdate(DeployInfo deployInfo) {
        deployInfo.setUtime(System.currentTimeMillis());
        if (deployInfo.getState() == DeployInfo.DeployState.nuke.ordinal()) {
            map.remove(deployInfo.getName());
        } else {
            map.put(deployInfo.getName(), deployInfo);
        }
    }

    public void recordContainerIds(List<String> ids) {
        DeployInfo di = new DeployInfo();
        di.setContainerIds(ids);
        map.put(container_config, di);
        this.save();
    }


    public List<String> getContainerIds() {
        return map.getOrDefault(container_config, new DeployInfo(Lists.newArrayList())).getContainerIds();
    }

    public void remove(DeployInfo deployInfo) {
        map.remove(deployInfo.getName());
    }


    public DeployInfo get(String name) {
        DeployInfo info = map.get(name);
        if (null == info) {
            DeployInfo deployInfo = new DeployInfo();
            deployInfo.setName(name);
            deployInfo.setId(name);
            deployInfo.setType(1);
            deployInfo.setPorts(com.google.common.collect.Lists.newArrayList());
            deployInfo.setCtime(System.currentTimeMillis());
            return deployInfo;
        }
        return info;
    }


    public DeployInfo getInfo(String key) {
        return this.map.get(key);
    }

    /**
     * 从磁盘加载
     */
    public void load() {
        log.info("load deploy info cache");
        try {
            File file = new File(cacheFilePath + cacheFileName);
            if (!file.exists()) {
                return;
            }

            byte[] data = Files.readAllBytes(Paths.get(cacheFilePath + cacheFileName));
            if (null != data && data.length > 0) {
                String jsonStr = new String(data);
                Type type = new TypeToken<Map<String, DeployInfo>>() {
                }.getType();
                Map<String, DeployInfo> tmpMap = new Gson().fromJson(jsonStr, type);
                tmpMap.entrySet().stream().forEach(it -> map.put(it.getKey(), it.getValue()));
                if (this.map.containsKey(config)) {
                    DeployInfo v = this.map.get(config);
                    log.info("port num:{}", v.getPortNum());
                    this.portNum.set(v.getPortNum() + 10);
                }
            }
        } catch (Throwable e) {
            log.warn("load cache error:" + e.getMessage(), e);
        }
        loadSreLabel();
    }

    /**
     * 加载sre设定的label
     */
    public void loadSreLabel() {
        Safe.execute(() -> {
            File file = new File(Config.ins().get("mione_json", "/etc/mione.json"));
            if (file.exists()) {
                String fileStr = new String(Files.readAllBytes(file.toPath()));
                this.sreLabel = new Gson().fromJson(fileStr, SreLabel.class);
                log.info("sre label:{}", this.sreLabel);
            }
        });
    }

    /**
     * 保存到磁盘
     */
    public void save() {
        log.info("save deploy info cache");
        try {
            DeployInfo di = new DeployInfo();
            di.setPortNum(this.portNum.get());
            this.map.put(config, di);
            Files.write(Paths.get(cacheFilePath + cacheFileName), new Gson().toJson(this.map).getBytes());
            log.info("save deploy info cache finish");
        } catch (Throwable ex) {
            log.warn("save ex:" + ex.getMessage(), ex);
        }
    }

    /**
     * 获取cpu数量
     *
     * @return
     */
    public int getCpuNum() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 大小是b
     *
     * @return
     */
    public long getMem() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osmxb.getTotalPhysicalMemorySize();
    }

    /**
     * 获取可以使用的cpu数量
     *
     * @return
     */
    public int getAvailableCpuNum() {
        Set<Integer> useCpus = this.map.values().stream().filter(it -> null != it.getContainerInfo() && null != it.getContainerInfo().getCpus() && it.getContainerInfo().getCpus().size() > 0).map(it -> it.getContainerInfo().getCpus()).flatMap(it -> it.stream()).collect(Collectors.toSet());

        return getCpuNum() - useCpus.size();
    }

    public List<DeployInfo> getPhysicalDeployInfos() {
        if (this.map.size() > 0) {
            return this.map.entrySet().stream().filter(it -> it.getValue().getType() == 0).map(it -> it.getValue()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<DeployInfo> getDockerDeployInfos() {
        if (this.map.size() > 0) {
            return this.map.entrySet().stream().filter(it -> it.getValue().getType() == 1).map(it -> it.getValue()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


}
