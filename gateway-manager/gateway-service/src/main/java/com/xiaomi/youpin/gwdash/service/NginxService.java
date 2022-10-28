///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.service;
//
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.listener.NamingEvent;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import com.dianping.cat.Cat;
//import com.google.gson.Gson;
//import com.xiaomi.data.push.nacos.NacosNaming;
//import com.xiaomi.youpin.gwdash.bo.DockerQueryParam;
//import com.xiaomi.youpin.gwdash.bo.MachineBo;
//import com.xiaomi.youpin.gwdash.bo.NginxConfigUpdateJob;
//import com.xiaomi.youpin.gwdash.bo.RemotingCommandBo;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.HttpService;
//import com.xiaomi.youpin.gwdash.exception.CommonException;
//import com.xiaomi.youpin.nginx.NginxUtils;
//import com.xiaomi.youpin.nginx.NginxUtilsV2;
//import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
//import com.xiaomi.youpin.tesla.agent.po.NginxInfo;
//import com.xiaomi.youpin.tesla.agent.po.NginxReq;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.tuple.Pair;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.nutz.dao.pager.Pager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.util.*;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.DelayQueue;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
///**
// * @author goodjava@qq.com
// * <p>
// * 可以动态修改nginx upstream 配置,达到服务治理的效果
// */
//@Service
//@Slf4j
//public class NginxService {
//
//
//    @Autowired
//    private NacosNaming nacosNaming;
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private NginxMachineSelector nginxMachineSelector;
//
//    @Autowired
//    private AgentManagerServiceWrapper agentManager;
//
//    @Autowired
//    private NacosService nacosService;
//
//    @Autowired
//    private ConfigService configService;
//
//    @Autowired
//    private FeiShuService feiShuService;
//
//    @Value("${feishu.nginx.chat.id}")
//    private String chatNginxId;
//
//    private final String dataId = "nginx_upstream";
//
//
//    /**
//     * v2是半自动的,必须用户触发才能走完全部流程 (snycConfigToNacos->deployConfig)
//     * 等v2成熟了,再从新开启v1(全自动化),不然风险不可控
//     */
//    private final String version = "v2";
//
//
//    private BlockingQueue<NginxConfigUpdateJob> configUpdateJobs = new DelayQueue<>();
//
//    private Runnable synRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            try {
//                log.info("synRunnable");
//                List<HttpService> list = getNginxService();
//                log.info("list:{}", list);
//                list.stream().forEach(it -> {
//                    try {
//                        List<Instance> instances = nacosNaming.getAllInstances(it.getServiceName());
//                        //nacos 上的 的实例
//                        List<String> addrs = getAddrsFromNacos(instances);
//                        log.info("addrs:{}", addrs);
//                        Syn(it, addrs);
//                    } catch (NacosException e) {
//                        log.warn("ex:{}", e.getMessage());
//                    }
//                });
//
//            } catch (Throwable ex) {
//                log.warn("error:{}", ex.getMessage());
//            }
//        }
//    };
//
//
//    @PostConstruct
//    public void init() {
//        log.info("NginxService init");
//
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(synRunnable, 10, 10, TimeUnit.SECONDS);
//
//        List<HttpService> list = getNginxService();
//        list.stream().forEach(it -> {
//            nacosNaming.subscribe(it.getServiceName(), event -> {
//                if (event instanceof NamingEvent) {
//                    NamingEvent ne = (NamingEvent) event;
//                    List<String> addrs = getAddrsFromNacos(ne.getInstances());
//                    log.info("naming on event list:{}", addrs);
//                    Syn(it, addrs);
//                }
//            });
//        });
//
//        Executors.newSingleThreadExecutor().submit(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        NginxConfigUpdateJob job = configUpdateJobs.take();
//                        updateNginxConfig(job.getNginxId(), job.getRemoveIpList());
//                    } catch (InterruptedException e) {
//                        log.error(e.toString());
//                        Cat.logError(e);
//                    } catch (Exception e) {
//                        log.error(e.toString());
//                        Cat.logError(e);
//                    }
//                }
//            }
//        });
//    }
//
//    public void addUpdateNginxConfigJob(long nginxId, long delaySeconds, List<String> removeIpList) {
//        configUpdateJobs.add(new NginxConfigUpdateJob(nginxId, delaySeconds, removeIpList));
//    }
//
//    private String getGroupNginxUpstreamNameConfig(String name, String group) {
//        try {
//            String nginxUpstreamNameConfig = nacosService.getConfig(getDataId(name), group, 3000);
//            if (!nginxUpstreamNameConfig.contains("upstream") || !nginxUpstreamNameConfig.contains("nginx配置")) {
//                log.error("nginxUpstreamNameConfig is wrong, {}", nginxUpstreamNameConfig);
//                return "";
//            }
//            return nginxUpstreamNameConfig;
//        } catch (NacosException e) {
//            log.error("getGroupNginxUpstreamNameConfig error, group: {}", group);
//        }
//        return "";
//    }
//
//    private List<MachineBo> getGroupMachineList(String group) {
//        List<MachineBo> machineList = nginxMachineSelector.select(DockerQueryParam.builder().build());
//
//        machineList.stream().forEach(it2 -> log.info("machine ip:{}", it2.getIp()));
//
//        if (machineList != null && machineList.size() > 0) {
//            machineList = machineList.stream().filter(it1 -> group.equals(it1.getGroup())).collect(Collectors.toList());
//        }
//
//        return machineList;
//    }
//
//    /**
//     * addrs 就是实际可用的地址
//     *
//     * @param it
//     * @param addrs
//     */
//    private void Syn(HttpService it, List<String> addrs) {
//
//        if (version.equals("v2")) {
//            return;
//        }
//
//        if (addrs == null || addrs.size() == 0) {
//            log.info("NginxService.Syn, params addrs is empty");
//            return;
//        }
//
//        List<MachineBo> machineList = this.getGroupMachineList(it.getGroup());
//        if (machineList == null || machineList.size() <= 0) {
//            log.info("nginx machine size = 0");
//            return;
//        }
//
//        String nginxUpstreamNameConfig = this.getGroupNginxUpstreamNameConfig(it.getUpstreamName(), it.getGroup());
//        if (StringUtils.isEmpty(nginxUpstreamNameConfig)) {
//            log.info("nginx upstream name config size = 0, or do not have key: {}", it.getGroup());
//            return;
//        }
//
//        try {
//            List<String> nginxAddrList = NginxUtils.getServers(nginxUpstreamNameConfig, it.getUpstreamName());
//            log.info("nginxAddrList:{}", nginxAddrList);
//
//            //key 是需要新增的  value 是需要删除的
//            Pair<List<String>, List<String>> diffList = diff(addrs, nginxAddrList);
//
//            log.info("diffList:{}", diffList);
//
//            if (diffList.getRight().size() > 0 || diffList.getLeft().size() > 0) {
//
//                if (diffList.getKey().size() > 0) {
//                    for (String it2 : diffList.getKey()) {
//                        nginxUpstreamNameConfig = NginxUtils.addServer(nginxUpstreamNameConfig, it.getUpstreamName(), it2);
//                    }
//                }
//
//                if (diffList.getValue().size() > 0) {
//                    for (String it3 : diffList.getValue()) {
//                        nginxUpstreamNameConfig = NginxUtils.removeServer(nginxUpstreamNameConfig, it.getUpstreamName(), it3);
//                    }
//                }
//
//                String newNginxUpstreamConfig = nginxUpstreamNameConfig;
//                if (configService.isNeedRefreshNginxUpstreamName()) {
//                    machineList.stream().forEach(it1 -> {
//                        Optional<String> port = getPort(it1.getIp());
//                        if (port.isPresent()) {
//                            int code = AgentCmd.nginxReq;
//                            NginxReq req2 = new NginxReq();
//                            req2.setCmd(NginxReq.modifyConfig);
//                            req2.setConfigPath(it.getConfigPath());
//                            req2.setUpstreamName(it.getUpstreamName());
//                            req2.setConfigStr(newNginxUpstreamConfig);
//                            req2.setNeedReload(true);
//                            String body2 = new Gson().toJson(req2);
//                            try {
//                                RemotingCommandBo res = agentManager.send(it1.getIp() + ":" + port.get(), code, body2, 1000);
//                                log.info("res:{}", res);
//                            } catch (Throwable ex) {
//                                log.error(ex.getMessage());
//                            }
//                        } else {
//                            log.warn("don't find port ip:{}", it1.getIp());
//                        }
//                    });
//                }
//
//                nacosService.publishConfig(dataId, it.getGroup(), newNginxUpstreamConfig);
//            }
//
//
//        } catch (Throwable ex) {
//            log.error(ex.getMessage());
//            return;
//        }
//    }
//
//    private List<String> getAddrsFromNacos(List<Instance> list) {
//        return getAddrsFromNacos(list, new HashSet<>());
//    }
//
//    private List<String> getAddrsFromNacos(List<Instance> list, HashSet<String> removedIpSet) {
//        // ip:port
//        return list.stream().filter(it1 -> isValidInstance(it1, removedIpSet)).map(it1 -> it1.getIp() + ":" + it1.getPort()).collect(Collectors.toList());
//    }
//
//    private boolean isValidInstance(Instance instance, HashSet<String> removedIpSet) {
//        return instance.isHealthy() && instance.isEnabled() && !removedIpSet.contains(instance.getIp());
//    }
//
//    private List<HttpService> getNginxService() {
//        return dao.query(HttpService.class, Cnd.where("status", "=", HttpService.STATUS_ON));
//    }
//
//    /**
//     * @param addrs      可用服务的地址(nacos上边注册的)
//     * @param nginxAddrs 目前nginx 配置的地址列表
//     * @return key:addList value:removeList
//     */
//    private Pair<List<String>, List<String>> diff(List<String> addrs, List<String> nginxAddrs) {
//
//        List<String> addList = addrs.stream().filter(it -> {
//            boolean find = nginxAddrs.stream().filter(it2 -> it2.contains(it)).findAny().isPresent();
//            return !find;
//        }).map(it -> "server " + it).collect(Collectors.toList());
//
//        List<String> removeList = nginxAddrs.stream().filter(it -> {
//            String addr = it.split("\\s+")[1];
//            return !addrs.contains(addr);
//        }).collect(Collectors.toList());
//
//        return Pair.of(addList, removeList);
//    }
//
//
//    private Optional<String> getPort(String ip) {
//        return agentManager.clientList().stream().filter(it -> it.contains(ip)).map(it -> it.split(":")[1]).findFirst();
//    }
//
//    public Result<Map<String, Object>> getNginxServiceList(String serviceName, int page, int pageSize) {
//        Cnd cnd = Cnd.where("service_name", "like", "%" + serviceName + "%").and("status", "=", HttpService.STATUS_ON);
//        Map<String, Object> map = new HashMap<>();
//        map.put("total", dao.count(HttpService.class, cnd));
//        map.put("list", dao.query(HttpService.class, cnd, new Pager(page, pageSize)));
//        return Result.success(map);
//    }
//
//    public Result<Boolean> delNginxService(long id) {
//        Cnd cnd = Cnd.where("id", "=", id);
//        HttpService httpService = dao.fetch(HttpService.class, cnd);
//        if (null != httpService) {
//            httpService.setStatus(HttpService.STATUS_DELETE);
//            dao.update(httpService);
//        }
//        return Result.success(true);
//    }
//
//    public Result<Boolean> createNginxService(String serviceName, String upstreamName, String configPath, String group) {
//        if (StringUtils.isEmpty(group)) {
//            return new Result<>(1, "创建失败，group不能为空", false);
//        }
//        if (null != dao.fetch(HttpService.class, Cnd.where("service_name", "=", serviceName).and("status", "=", HttpService.STATUS_ON))) {
//            return new Result<>(1, "创建失败，serviceName重复", false);
//        }
//        if (null != dao.fetch(HttpService.class, Cnd.where("upstream_name", "=", upstreamName).and("group", "=", group).and("status", "=", HttpService.STATUS_ON))) {
//            return new Result<>(1, "创建失败，upstreamName重复", false);
//        }
//        long now = System.currentTimeMillis();
//        HttpService httpService = new HttpService();
//        httpService.setServiceName(serviceName);
//        httpService.setUpstreamName(upstreamName);
//        httpService.setConfigPath(configPath);
//        httpService.setStatus(HttpService.STATUS_ON);
//        httpService.setGroup(group);
//        httpService.setCtime(now);
//        httpService.setUtime(now);
//        try {
//            dao.insert(httpService);
//        } catch (Exception e) {
//            return new Result<>(1, "创建失败，请检查参数", false);
//        }
//        return Result.success(true);
//    }
//
//    public Result<Boolean> createNginxService(long envId, long port, boolean sdk, String serviceName, String upstreamName, String configPath, String group) {
//        if (StringUtils.isEmpty(group)) {
//            return new Result<>(1, "创建失败，group不能为空", false);
//        }
//        if (null != dao.fetch(HttpService.class, Cnd.where("service_name", "=", serviceName).and("status", "=", HttpService.STATUS_ON))) {
//            return new Result<>(1, "创建失败，serviceName重复", false);
//        }
//        if (null != dao.fetch(HttpService.class, Cnd.where("upstream_name", "=", upstreamName).and("group", "=", group).and("status", "=", HttpService.STATUS_ON))) {
//            return new Result<>(1, "创建失败，upstreamName重复", false);
//        }
//        long now = System.currentTimeMillis();
//        HttpService httpService = new HttpService();
//        httpService.setEnvId(envId);
//        httpService.setPort(port);
//        httpService.setServiceName(serviceName);
//        httpService.setUpstreamName(upstreamName);
//        httpService.setConfigPath(configPath);
//        httpService.setStatus(HttpService.STATUS_ON);
//        httpService.setSdk(sdk);
//        httpService.setGroup(group);
//        httpService.setCtime(now);
//        httpService.setUtime(now);
//        try {
//            dao.insert(httpService);
//        } catch (Exception e) {
//            return new Result<>(1, "创建失败，请检查参数", false);
//        }
//        return Result.success(true);
//    }
//
//    public Result<Boolean> editNginxService(long id, String serviceName, String upstreamName, String configPath, String group) {
//        if (StringUtils.isEmpty(group)) {
//            return new Result<>(1, "编辑失败，group不能为空", false);
//        }
//        if (null != dao.fetch(HttpService.class, Cnd.where("service_name", "=", serviceName).and("status", "=", HttpService.STATUS_ON).and("id", "!=", id))) {
//            return new Result<>(1, "编辑失败，serviceName重复", false);
//        }
//        if (null != dao.fetch(HttpService.class, Cnd.where("upstream_name", "=", upstreamName).and("group", "=", group).and("status", "=", HttpService.STATUS_ON).and("id", "!=", id))) {
//            return new Result<>(1, "编辑失败，upstreamName重复", false);
//        }
//
//        Cnd cnd = Cnd.where("id", "=", id);
//        HttpService httpService = dao.fetch(HttpService.class, cnd);
//        if (null != httpService) {
//            httpService.setUpstreamName(upstreamName);
//            httpService.setServiceName(serviceName);
//            httpService.setGroup(group);
//            httpService.setConfigPath(configPath);
//            httpService.setUtime(System.currentTimeMillis());
//            dao.update(httpService);
//        }
//        return Result.success(true);
//    }
//
//    public Result<Boolean> editNginxService(long id, long port, boolean sdk, String serviceName, String upstreamName, String configPath, String group) {
//        if (StringUtils.isEmpty(group)) {
//            return new Result<>(1, "编辑失败，group不能为空", false);
//        }
//        if (null != dao.fetch(HttpService.class, Cnd.where("service_name", "=", serviceName).and("status", "=", HttpService.STATUS_ON).and("id", "!=", id))) {
//            return new Result<>(1, "编辑失败，serviceName重复", false);
//        }
//        if (null != dao.fetch(HttpService.class, Cnd.where("upstream_name", "=", upstreamName).and("group", "=", group).and("status", "=", HttpService.STATUS_ON).and("id", "!=", id))) {
//            return new Result<>(1, "编辑失败，upstreamName重复", false);
//        }
//
//        Cnd cnd = Cnd.where("id", "=", id);
//        HttpService httpService = dao.fetch(HttpService.class, cnd);
//        if (null != httpService) {
//            httpService.setPort(port);
//            httpService.setSdk(sdk);
//            httpService.setGroup(group);
//            httpService.setUpstreamName(upstreamName);
//            httpService.setServiceName(serviceName);
//            httpService.setConfigPath(configPath);
//            httpService.setUtime(System.currentTimeMillis());
//            dao.update(httpService);
//        }
//        return Result.success(true);
//    }
//
//    /**
//     * 保存配置到nacos
//     */
//    public String snycConfig2Nacos(HttpService httpService) {
//        return snycConfig2Nacos(httpService, new LinkedList<>());
//    }
//
//    /**
//     * 保存配置到nacos
//     */
//    public String snycConfig2Nacos(HttpService httpService, List<String> removedIpList) {
//        try {
//
//            HashSet<String> removedIpSet = new HashSet<>();
//            removedIpSet.addAll(removedIpList);
//
//
//            List<Instance> instances = nacosNaming.getAllInstances(httpService.getServiceName());
//            //nacos 上的 的实例
//            List<String> addrs = getAddrsFromNacos(instances, removedIpSet);
//
//            String nginxUpstreamNameConfig = this.getGroupNginxUpstreamNameConfig(httpService.getUpstreamName(), httpService.getGroup());
//
//            if (StringUtils.isEmpty(nginxUpstreamNameConfig)) {
//                return null;
//            }
//            //新的配置
//            String newConfig = NginxUtilsV2.addServer(nginxUpstreamNameConfig, httpService.getUpstreamName(), addrs);
//
//            nacosService.publishConfig(getDataId(httpService.getUpstreamName()), httpService.getGroup(), newConfig);
//            return newConfig;
//        } catch (Throwable ex) {
//            log.error(ex.getMessage());
//        }
//        return null;
//    }
//
//    /**
//     * 保存配置到nacos
//     * 直接用updateIpList去更新nacos
//     */
//    public String snycConfig2NacosByIpList(long id, List<String> updateIpList) {
//        if (updateIpList == null || updateIpList.size() == 0) {
//            log.info("NginxService.snycConfig2NacosByIpList, updateIpList is empty");
//            return null;
//        }
//
//        try {
//            HttpService httpService = dao.fetch(HttpService.class, id);
//
//            String nginxUpstreamNameConfig = this.getGroupNginxUpstreamNameConfig(httpService.getUpstreamName(), httpService.getGroup());
//            if (StringUtils.isEmpty(nginxUpstreamNameConfig)) {
//                return null;
//            }
//
//            String newConfig = NginxUtilsV2.addServer(nginxUpstreamNameConfig, httpService.getUpstreamName(), updateIpList);
//            nacosService.publishConfig(getDataId(httpService.getUpstreamName()), httpService.getGroup(), newConfig);
//
//            return newConfig;
//        } catch (Throwable ex) {
//            log.error(ex.getMessage());
//        }
//        return null;
//    }
//
//    /**
//     * 保存配置到nacos
//     */
//    public String snycConfig2NacosById(long id) {
//        return snycConfig2NacosById(id, new LinkedList<>());
//    }
//
//    /**
//     * 保存配置到nacos
//     */
//    public String snycConfig2NacosById(long id, List<String> removeIpList) {
//        try {
//            HttpService httpService = dao.fetch(HttpService.class, id);
//            if (null != httpService) {
//                return snycConfig2Nacos(httpService, removeIpList);
//            }
//        } catch (Throwable ex) {
//            log.error(ex.getMessage());
//        }
//        return null;
//    }
//
//    private String getDataId(String name) {
//        return Stream.of(dataId, name).collect(Collectors.joining("_"));
//
//    }
//
//    /**
//     * 发布配置(从nacos到nginx机器)
//     */
//    public void deployConfig2Nginx(HttpService httpService) {
//        String config = getGroupNginxUpstreamNameConfig(httpService.getUpstreamName(), httpService.getGroup());
//        deployConfig2Nginx(httpService, config);
//    }
//
//    /**
//     * 发布配置(从nacos到nginx机器)
//     */
//    public void deployConfig2Nginx(HttpService httpService, String config) {
//        List<MachineBo> machineList = this.getGroupMachineList(httpService.getGroup());
//        if (machineList == null || machineList.size() <= 0) {
//            log.info("nginx machine size = 0");
//            return;
//        }
//
//        sendFeishu(httpService, machineList);
//
//        machineList.stream().forEach(it1 -> {
//            Optional<String> port = getPort(it1.getIp());
//            if (port.isPresent()) {
//                int code = AgentCmd.nginxReq;
//                NginxReq req2 = new NginxReq();
//                req2.setCmd(NginxReq.modifyConfig);
//                req2.setConfigPath(httpService.getConfigPath());
//                req2.setUpstreamName(httpService.getUpstreamName());
//                req2.setConfigStr(config);
//                req2.setNeedReload(true);
//                String body2 = new Gson().toJson(req2);
//                try {
//                    RemotingCommandBo res = agentManager.send(it1.getIp() + ":" + port.get(), code, body2, 2000);
//                    log.info("res:{}", res);
//                } catch (Throwable ex) {
//                    log.error(ex.getMessage());
//                }
//            } else {
//                log.warn("deployConfig don't find port ip:{}", it1.getIp());
//            }
//        });
//    }
//
//    /**
//     * 发布配置(从nacos到nginx机器)
//     */
//    public void deployConfig2NginxById(long id) {
//        HttpService httpService = dao.fetch(HttpService.class, id);
//        if (null != httpService) {
//            deployConfig2Nginx(httpService);
//        }
//    }
//
//    /**
//     * 发布配置(从nacos到nginx机器)
//     */
//    public void deployConfig2NginxById(long id, String nginxConfig) {
//        HttpService httpService = dao.fetch(HttpService.class, id);
//        if (null != httpService) {
//            deployConfig2Nginx(httpService, nginxConfig);
//        }
//    }
//
//    /**
//     * 发布配置(从nacos到nginx机器)
//     */
//    public void updateNginxConfig(long nginxId, List<String> removeIpList) {
//        log.info("update nginx");
//        String nginxConfig = snycConfig2NacosById(nginxId, removeIpList);
//        deployConfig2NginxById(nginxId, nginxConfig);
//    }
//
//
//    /**
//     * 发布nginx时需要给飞书来一条通知
//     */
//    private void sendFeishu(HttpService httpService, List<MachineBo> machineList) {
//        try {
//            List<String> nginxIps = machineList.stream().map(MachineBo::getIp).collect(Collectors.toList());
//            List<String> nginxHostnames = machineList.stream().map(MachineBo::getHostname).collect(Collectors.toList());
//            String msg = "发布nginx配置\n" + "\n"
//                + "serviceName: " + httpService.getServiceName() + "\n"
//                + "nginx配置路径: " + httpService.getConfigPath() + "\n"
//                + "nginxUpstreamName: " + httpService.getUpstreamName() + "\n"
//                + "nginx机器ip列表: " + String.join(", ", nginxIps) + "\n"
//                + "nginx机器hostname列表: " + String.join(", ", nginxHostnames) + "\n";
//            feiShuService.sendMsg("", msg, chatNginxId);
//        } catch (Exception e) {
//            log.error("NginxService.sendFeishu, something wrong, msg: {}", e.getMessage());
//        }
//    }
//
//
//    public Result<List<MachineBo>> getNginxMachine(String group) {
//        if (StringUtils.isEmpty(group)) {
//            return Result.fail(new CommonException(1, "group参数不能为空"));
//        }
//        List<MachineBo> machineList = this.getGroupMachineList(group);
//        return Result.success(machineList);
//    }
//
//    public Result<List<String>> getUpstreamNameDetail(String ip, String configPath, String upstreamName) {
//        Optional<String> portOptional = getPort(ip);
//
//        if (!portOptional.isPresent()) {
//            log.warn("port is null");
//            return Result.fail(new CommonException(1, "machine port is null"));
//        }
//
//        NginxReq req = new NginxReq();
//        req.setCmd(NginxReq.info);
//        req.setConfigPath(configPath);
//        req.setUpstreamName(upstreamName);
//        String body = new Gson().toJson(req);
//
//        RemotingCommandBo remotingCommand = agentManager.send(ip + ":" + portOptional.get(), AgentCmd.nginxReq, body, 3000);
//
//        NginxInfo info = new Gson().fromJson(new String(remotingCommand.getBody()),NginxInfo.class);// remotingCommand.getReq(NginxInfo.class);
//        if (info.getCode() != 0) {
//            return Result.fail(new CommonException(info.getCode(), info.getMsg()));
//        }
//        List<String> nginxAddrList = info.getAddrList();
//        log.info("nginxAddrList:{}", nginxAddrList);
//
//        return Result.success(nginxAddrList);
//    }
//
//    public Result<HttpService> getNginxServiceByEnvId(long envId) {
//        return Result.success(dao.fetch(HttpService.class, Cnd.where("env_id", "=", envId).and("status", "=", HttpService.STATUS_ON)));
//    }
//}
