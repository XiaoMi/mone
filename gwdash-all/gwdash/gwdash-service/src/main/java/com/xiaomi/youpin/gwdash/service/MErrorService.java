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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.agent.AgentManager;
import com.xiaomi.youpin.gwdash.bo.ScaleType;
import com.xiaomi.youpin.gwdash.common.LockUtils;
import com.xiaomi.youpin.gwdash.common.MSafe;
import com.xiaomi.youpin.gwdash.dao.model.ErrorContent;
import com.xiaomi.youpin.gwdash.dao.model.MError;
import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.po.DockerCmd;
import com.xiaomi.youpin.tesla.agent.po.ServiceReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sun.tools.jstat.Scale;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * <p>
 * 错误处理
 * <p>
 * 可以用来保障daemonset
 */
@Slf4j
@Service
@Component
public class MErrorService {


    @Autowired
    private Dao dao;


    @Autowired
    private AgentManager agentManager;


    @Autowired
    private ProjectDeploymentService deploymentService;


    @Autowired
    private LockUtils lockUtils;


    public void insert(MError error) {
        dao.insert(error);
    }


    public void batchInsert(List<MError> list) {
        if (list.size() == 0) {
            return;
        }
        dao.insert(list);
    }


    public boolean delete(List<Integer> id) {
        int len = dao.clear(MError.class, Cnd.where("id", "in", id));
        return len > 0;
    }

    /**
     * 分页查询
     * 需要传入:pageNumber pageSize
     *
     * @param pager
     * @return
     */
    public List<MError> list(Pager pager) {
        int count = dao.count(MError.class);
        pager.setRecordCount(count);
        return dao.query(MError.class, Cnd.orderBy().desc("id"), pager);
    }

    public int getTotal() {
        return dao.count(MError.class);
    }


    @PostConstruct
    public void init() {
        ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
        //错误处理,让是修复一些问题
        pool.scheduleAtFixedRate(() -> {
            try {
                //负载过高的问题
                List<MError> loadHighError = dao.query(MError.class, Cnd.where("status", "=", 0).and("type", "=", MError.ErrorType.HealthCheckLoadHigh.ordinal()));
                //尝试扩容
                autoScale(loadHighError, ScaleType.expansion);

                //负载过低
                List<MError> loadLowError = dao.query(MError.class, Cnd.where("status", "=", 0).and("type", "=", MError.ErrorType.HealthCheckLoadLow.ordinal()));
                //尝试缩容
                autoScale(loadLowError, ScaleType.shrink);

                //服务器挂了
                List<MError> errors = dao.query(MError.class, Cnd.where("status", "=", 0).and("type", "=", MError.ErrorType.HealthCheck.ordinal()));
                restart(errors);

                //nuke不需要的服务
                List<MError> nukes = dao.query(MError.class, Cnd.where("status", "=", 0).and("type", "=", MError.ErrorType.Nuke.ordinal()));
                nuke(nukes);
            } catch (Throwable ex) {
                log.warn("error:{}", ex.getMessage());
            }
        }, 5, 20, TimeUnit.SECONDS);
    }

    /**
     * 从新拉起机器
     *
     * @param errors
     */
    private void restart(List<MError> errors) {
        if (true) {
            return;
        }
        long now = System.currentTimeMillis();
        errors.stream().forEach(it -> {
            try {
                log.info("rollback:{}", it);
                //健康监测发现服务出现了问题,从新拉起服务
                if (it.getType() == MError.ErrorType.HealthCheck.ordinal()) {
                    ServiceReq serviceReq = new ServiceReq();
                    serviceReq.setCmd(DockerCmd.restart.name());

                    ErrorContent content = it.getContent();

                    ProjectPipeline pipeline = dao.fetch(ProjectPipeline.class, content.getPipelineId());
                    //副本数量为0了,就不再尝试唤醒了
                    if (pipeline.getDeploySetting().getDockerReplicate() == 0) {
                        return;
                    }

                    String key = LockUtils.deployKey(pipeline.getEnvId());

                    boolean lock = lockUtils.tryLock(key);

                    //有发布中的,不在进行拉起操作
                    if (!lock) {
                        return;
                    }

                    try {
                        dao.fetchLinks(pipeline, null);

                        serviceReq.setServicePath(pipeline.getDeploySetting().getDeploySettingPath());
                        serviceReq.setHeapSize(String.valueOf(pipeline.getDeploySetting().getDeploySettingHeapSize()));
                        serviceReq.setJarName(pipeline.getProjectCompileRecord().getJarName());
                        String address = getRemoteAddress(it.getIp());
                        if (StringUtils.isEmpty(address)) {
                            return;
                        }
                        agentManager.send(address, AgentCmd.dockerReq, new Gson().toJson(serviceReq), 3000, responseFuture -> {
                        });
                    } finally {
                        lockUtils.unLock(key);
                    }
                }
            } catch (Throwable ex) {
                log.error("id:{} error:{}", it.getId(), ex.getMessage());
            } finally {
                it.setStatus(1);
                it.setUtime(now);
                dao.update(it);
            }
        });
    }

    private String getRemoteAddress(String ip) {
        return agentManager.getClientAddress(ip).orElse("");
    }

    /**
     * 进行自动扩容
     *
     * @param loadHighError
     */
    private void autoScale(List<MError> loadHighError, ScaleType type) {
        loadHighError.stream().forEach(it -> {
            int status = 1;
            try {
                ErrorContent content = it.getContent();
                deploymentService.autoScale(String.valueOf(content.getEnvId()), type.name());
            } catch (Throwable ex) {
                log.warn("autoScale:{} error:{}", type, ex.getMessage());
                status = 2;
            } finally {
                it.setStatus(status);
                it.setUtime(System.currentTimeMillis());
                dao.update(it);
            }
        });
    }

    /**
     * nuke 掉程序  (先用 shutdown)
     *
     * @param eventList
     */
    private void nuke(List<MError> eventList) {
        if (true) {
            return;
        }
        eventList.stream().forEach(it -> {
            List<String> ips = it.getContent().getIps();
            ips.stream().forEach(ip -> {
                log.info("nuke:{}", ip);
                String address = getRemoteAddress(ip);
                if (StringUtils.isEmpty(address)) {
                    log.warn("address is null:{}", ip);
                    return;
                }
                ServiceReq serviceReq = new ServiceReq();
                Map<String, String> att = Maps.newHashMap();
                att.put("type", "mErrorService_nuke");
                serviceReq.setAttachments(att);
                serviceReq.setCmd(DockerCmd.shutdown.name());
                serviceReq.setJarName(it.getContent().getName());
                MSafe.execute(() -> agentManager.send(address, AgentCmd.dockerReq, new Gson().toJson(serviceReq), 1000, responseFuture -> {
                }));
            });
            it.setUtime(System.currentTimeMillis());
            it.setStatus(1);
            dao.update(it);
        });
    }


}
