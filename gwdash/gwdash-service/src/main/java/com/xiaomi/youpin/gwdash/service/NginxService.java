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

package com.xiaomi.youpin.gwdash.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.gson.Gson;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.gwdash.agent.AgentManager;
import com.xiaomi.youpin.gwdash.bo.DockerQueryParam;
import com.xiaomi.youpin.gwdash.bo.MachineBo;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.HttpService;
import com.xiaomi.youpin.gwdash.exception.CommonException;
import com.xiaomi.youpin.nginx.NginxUtils;
import com.xiaomi.youpin.nginx.NginxUtilsV2;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.po.NginxInfo;
import com.xiaomi.youpin.tesla.agent.po.NginxReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 * <p>
 * 可以动态修改nginx upstream 配置,达到服务治理的效果
 */
@Service
@Slf4j
public class NginxService {


    @Autowired
    private NacosNaming nacosNaming;

    @Autowired
    private Dao dao;

    @Autowired
    private NginxMachineSelector nginxMachineSelector;

    @Autowired
    private AgentManager agentManager;

    @Autowired
    private NacosServiceImpl nacosService;

    @Autowired
    private ConfigService configService;

    private final String dataId = "nginx_upstream";


    /**
     * v2是半自动的,必须用户触发才能走完全部流程 (snycConfigToNacos->deployConfig)
     * 等v2成熟了,再从新开启v1(全自动化),不然风险不可控
     */
    private final String version = "v2";


    private Runnable synRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                log.info("synRunnable");
                List<HttpService> list = getNginxService();
                log.info("list:{}", list);
                list.stream().forEach(it -> {
                    try {
                        List<Instance> instances = nacosNaming.getAllInstances(it.getServiceName());
                        //nacos 上的 的实例
                        List<String> addrs = getAddrsFromNacos(instances);
                        log.info("addrs:{}", addrs);
                        Syn(it, addrs);
                    } catch (NacosException e) {
                        log.warn("ex:{}", e.getMessage());
                    }
                });

            } catch (Throwable ex) {
                log.warn("error:{}", ex.getMessage());
            }
        }
    };


    @PostConstruct
    public void init() {
        log.info("NginxService init");

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(synRunnable, 10, 10, TimeUnit.SECONDS);

        List<HttpService> list = getNginxService();
        list.stream().forEach(it -> {
            nacosNaming.subscribe(it.getServiceName(), event -> {
                if (event instanceof NamingEvent) {
                    NamingEvent ne = (NamingEvent) event;
                    List<String> addrs = getAddrsFromNacos(ne.getInstances());
                    log.info("naming on event list:{}", addrs);
                    Syn(it, addrs);
                }
            });
        });
    }

    private String getGroupNginxUpstreamNameConfig(String name, String group) {
        try {
            String nginxUpstreamNameConfig = nacosService.getConfig(getDataId(name), group, 3000);
            if (!nginxUpstreamNameConfig.contains("upstream") || !nginxUpstreamNameConfig.contains("nginx配置")) {
                log.error("nginxUpstreamNameConfig is wrong, {}", nginxUpstreamNameConfig);
                return "";
            }
            return nginxUpstreamNameConfig;
        } catch (NacosException e) {
            log.error("getGroupNginxUpstreamNameConfig error, group: {}", group);
        }
        return "";
    }

    private List<MachineBo> getGroupMachineList(String group) {
        List<MachineBo> machineList = nginxMachineSelector.select(DockerQueryParam.builder().build());

        machineList.stream().forEach(it2 -> log.info("machine ip:{}", it2.getIp()));

        if (machineList != null && machineList.size() > 0) {
            machineList = machineList.stream().filter(it1 -> group.equals(it1.getGroup())).collect(Collectors.toList());
        }

        return machineList;
    }

    /**
     * addrs 就是实际可用的地址
     *
     * @param it
     * @param addrs
     */
    private void Syn(HttpService it, List<String> addrs) {

        if (version.equals("v2")) {
            return;
        }

        List<MachineBo> machineList = this.getGroupMachineList(it.getGroup());
        if (machineList == null || machineList.size() <= 0) {
            log.info("nginx machine size = 0");
            return;
        }

        String nginxUpstreamNameConfig = this.getGroupNginxUpstreamNameConfig(it.getUpstreamName(), it.getGroup());
        if (StringUtils.isEmpty(nginxUpstreamNameConfig)) {
            log.info("nginx upstream name config size = 0, or do not have key: {}", it.getGroup());
            return;
        }

        try {
            List<String> nginxAddrList = NginxUtils.getServers(nginxUpstreamNameConfig, it.getUpstreamName());
            log.info("nginxAddrList:{}", nginxAddrList);

            //key 是需要新增的  value 是需要删除的
            Pair<List<String>, List<String>> diffList = diff(addrs, nginxAddrList);

            log.info("diffList:{}", diffList);

            if (diffList.getRight().size() > 0 || diffList.getLeft().size() > 0) {

                if (diffList.getKey().size() > 0) {
                    for (String it2 : diffList.getKey()) {
                        nginxUpstreamNameConfig = NginxUtils.addServer(nginxUpstreamNameConfig, it.getUpstreamName(), it2);
                    }
                }

                if (diffList.getValue().size() > 0) {
                    for (String it3 : diffList.getValue()) {
                        nginxUpstreamNameConfig = NginxUtils.removeServer(nginxUpstreamNameConfig, it.getUpstreamName(), it3);
                    }
                }

                String newNginxUpstreamConfig = nginxUpstreamNameConfig;
                if (configService.isNeedRefreshNginxUpstreamName()) {
                    machineList.stream().forEach(it1 -> {
                        Optional<String> port = getPort(it1.getIp());
                        if (port.isPresent()) {
                            int code = AgentCmd.nginxReq;
                            NginxReq req2 = new NginxReq();
                            req2.setCmd(NginxReq.modifyConfig);
                            req2.setConfigPath(it.getConfigPath());
                            req2.setUpstreamName(it.getUpstreamName());
                            req2.setConfigStr(newNginxUpstreamConfig);
                            req2.setNeedReload(true);
                            String body2 = new Gson().toJson(req2);
                            try {
                                RemotingCommand res = agentManager.send(it1.getIp() + ":" + port.get(), code, body2, 1000);
                                log.info("res:{}", res);
                            } catch (Throwable ex) {
                                log.error(ex.getMessage());
                            }
                        } else {
                            log.warn("don't find port ip:{}", it1.getIp());
                        }
                    });
                }

                nacosService.publishConfig(dataId, it.getGroup(), newNginxUpstreamConfig);
            }


        } catch (Throwable ex) {
            log.error(ex.getMessage());
            return;
        }
    }

    private List<String> getAddrsFromNacos(List<Instance> list) {
        // ip:port
        return list.stream().filter(it1 -> (it1.isHealthy() && it1.isEnabled())).map(it1 -> it1.getIp() + ":" + it1.getPort()).collect(Collectors.toList());
    }

    private List<HttpService> getNginxService() {
        return dao.query(HttpService.class, Cnd.where("status", "=", HttpService.STATUS_ON));
    }

    /**
     * @param addrs      可用服务的地址(nacos上边注册的)
     * @param nginxAddrs 目前nginx 配置的地址列表
     * @return key:addList value:removeList
     */
    private Pair<List<String>, List<String>> diff(List<String> addrs, List<String> nginxAddrs) {

        List<String> addList = addrs.stream().filter(it -> {
            boolean find = nginxAddrs.stream().filter(it2 -> it2.contains(it)).findAny().isPresent();
            return !find;
        }).map(it -> "server " + it).collect(Collectors.toList());

        List<String> removeList = nginxAddrs.stream().filter(it -> {
            String addr = it.split("\\s+")[1];
            return !addrs.contains(addr);
        }).collect(Collectors.toList());

        return Pair.of(addList, removeList);
    }


    private Optional<String> getPort(String ip) {
        return agentManager.clientList().stream().filter(it -> it.contains(ip)).map(it -> it.split(":")[1]).findFirst();
    }

    public Result<Map<String, Object>> getNginxServiceList(String serviceName, int page, int pageSize) {
        Cnd cnd = Cnd.where("service_name", "like", "%" + serviceName + "%").and("status", "=", HttpService.STATUS_ON);
        Map<String, Object> map = new HashMap<>();
        map.put("total", dao.count(HttpService.class, cnd));
        map.put("list", dao.query(HttpService.class, cnd, new Pager(page, pageSize)));
        return Result.success(map);
    }

    public Result<Boolean> delNginxService(long id) {
        Cnd cnd = Cnd.where("id", "=", id);
        HttpService httpService = dao.fetch(HttpService.class, cnd);
        if (null != httpService) {
            httpService.setStatus(HttpService.STATUS_DELETE);
            dao.update(httpService);
        }
        return Result.success(true);
    }

    public Result<Boolean> createNginxService(String serviceName, String upstreamName, String configPath, String group) {
        if (StringUtils.isEmpty(group)) {
            return new Result<>(1, "创建失败，group不能为空", false);
        }
        if (null != dao.fetch(HttpService.class, Cnd.where("service_name", "=", serviceName).and("status", "=", HttpService.STATUS_ON))) {
            return new Result<>(1, "创建失败，serviceName重复", false);
        }
        if (null != dao.fetch(HttpService.class, Cnd.where("upstream_name", "=", upstreamName).and("group", "=", group).and("status", "=", HttpService.STATUS_ON))) {
            return new Result<>(1, "创建失败，upstreamName重复", false);
        }
        long now = System.currentTimeMillis();
        HttpService httpService = new HttpService();
        httpService.setServiceName(serviceName);
        httpService.setUpstreamName(upstreamName);
        httpService.setConfigPath(configPath);
        httpService.setStatus(HttpService.STATUS_ON);
        httpService.setGroup(group);
        httpService.setCtime(now);
        httpService.setUtime(now);
        try {
            dao.insert(httpService);
        } catch (Exception e) {
            return new Result<>(1, "创建失败，请检查参数", false);
        }
        return Result.success(true);
    }

    public Result<Boolean> editNginxService(long id, String serviceName, String upstreamName, String configPath, String group) {
        if (StringUtils.isEmpty(group)) {
            return new Result<>(1, "编辑失败，group不能为空", false);
        }
        if (null != dao.fetch(HttpService.class, Cnd.where("service_name", "=", serviceName).and("status", "=", HttpService.STATUS_ON).and("id", "!=", id))) {
            return new Result<>(1, "编辑失败，serviceName重复", false);
        }
        if (null != dao.fetch(HttpService.class, Cnd.where("upstream_name", "=", upstreamName).and("group", "=", group).and("status", "=", HttpService.STATUS_ON).and("id", "!=", id))) {
            return new Result<>(1, "编辑失败，upstreamName重复", false);
        }

        Cnd cnd = Cnd.where("id", "=", id);
        HttpService httpService = dao.fetch(HttpService.class, cnd);
        if (null != httpService) {
            httpService.setUpstreamName(upstreamName);
            httpService.setServiceName(serviceName);
            httpService.setGroup(group);
            httpService.setConfigPath(configPath);
            httpService.setUtime(System.currentTimeMillis());
            dao.update(httpService);
        }
        return Result.success(false);
    }

    /**
     * 保存配置到nacos
     */
    public void snycConfig2Nacos(HttpService httpService) {
        try {
            List<Instance> instances = nacosNaming.getAllInstances(httpService.getServiceName());
            //nacos 上的 的实例
            List<String> addrs = getAddrsFromNacos(instances);

            String nginxUpstreamNameConfig = this.getGroupNginxUpstreamNameConfig(httpService.getUpstreamName(), httpService.getGroup());
            if (StringUtils.isEmpty(nginxUpstreamNameConfig)) {
                return;
            }
            //新的配置
            String newConfig = NginxUtilsV2.addServer(nginxUpstreamNameConfig, httpService.getUpstreamName(), addrs);
            nacosService.publishConfig(getDataId(httpService.getUpstreamName()), httpService.getGroup(), newConfig);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }


    private String getDataId(String name) {
        return Stream.of(dataId, name).collect(Collectors.joining("_"));

    }

    /**
     * 发布配置(从nacos到nginx机器)
     */
    public void deployConfig2Nginx(HttpService httpService) {
        String config = getGroupNginxUpstreamNameConfig(httpService.getUpstreamName(), httpService.getGroup());

        List<MachineBo> machineList = this.getGroupMachineList(httpService.getGroup());
        if (machineList == null || machineList.size() <= 0) {
            log.info("nginx machine size = 0");
            return;
        }

        machineList.stream().forEach(it1 -> {
            Optional<String> port = getPort(it1.getIp());
            if (port.isPresent()) {
                int code = AgentCmd.nginxReq;
                NginxReq req2 = new NginxReq();
                req2.setCmd(NginxReq.modifyConfig);
                req2.setConfigPath(httpService.getConfigPath());
                req2.setUpstreamName(httpService.getUpstreamName());
                req2.setConfigStr(config);
                req2.setNeedReload(true);
                String body2 = new Gson().toJson(req2);
                try {
                    RemotingCommand res = agentManager.send(it1.getIp() + ":" + port.get(), code, body2, 1000);
                    log.info("res:{}", res);
                } catch (Throwable ex) {
                    log.error(ex.getMessage());
                }
            } else {
                log.warn("deployConfig don't find port ip:{}", it1.getIp());
            }
        });
    }

    public Result<List<MachineBo>> getNginxMachine(String group) {
        if (StringUtils.isEmpty(group)) {
            return Result.fail(new CommonException(1, "group参数不能为空"));
        }
        List<MachineBo> machineList = this.getGroupMachineList(group);
        return Result.success(machineList);
    }

    public Result<List<String>> getUpstreamNameDetail(String ip, String configPath, String upstreamName) {
        Optional<String> portOptional = getPort(ip);

        if (!portOptional.isPresent()) {
            log.warn("port is null");
            return Result.fail(new CommonException(1, "machine port is null"));
        }

        NginxReq req = new NginxReq();
        req.setCmd(NginxReq.info);
        req.setConfigPath(configPath);
        req.setUpstreamName(upstreamName);
        String body = new Gson().toJson(req);

        RemotingCommand remotingCommand = agentManager.send(ip + ":" + portOptional.get(), AgentCmd.nginxReq, body, 3000);

        NginxInfo info = remotingCommand.getReq(NginxInfo.class);
        if (info.getCode() != 0) {
            return Result.fail(new CommonException(info.getCode(), info.getMsg()));
        }
        List<String> nginxAddrList = info.getAddrList();
        log.info("nginxAddrList:{}", nginxAddrList);

        return Result.success(nginxAddrList);
    }
}
