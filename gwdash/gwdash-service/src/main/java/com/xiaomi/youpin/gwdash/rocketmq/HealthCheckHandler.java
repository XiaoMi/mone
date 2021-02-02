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

package com.xiaomi.youpin.gwdash.rocketmq;

import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.bo.ScaleType;
import com.xiaomi.youpin.gwdash.common.GwCache;
import com.xiaomi.youpin.gwdash.common.LabelUtils;
import com.xiaomi.youpin.gwdash.dao.model.ErrorContent;
import com.xiaomi.youpin.gwdash.dao.model.MError;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnvDeploySetting;
import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
import com.xiaomi.youpin.gwdash.service.PipelineService;
import com.xiaomi.youpin.mischedule.api.service.bo.HealthResult;
import com.xiaomi.youpin.mischedule.api.service.bo.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author renqingfu
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class HealthCheckHandler {

    @Autowired
    private Dao dao;

    @Autowired
    private PipelineService pipelineService;


    @Value("${rocket.tag.healthcheck}")
    private String healthCheckTag;

    @Autowired
    private GwCache gwCache;

    public static String tag;


    private ReentrantLock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        tag = healthCheckTag;
    }

    /**
     * 存储健康监测结果
     *
     * @param message
     */
    public void consumeMessage(MessageExt message) {
        log.info("HealthCheckHandler#consumeMessage: {} {}", message.getMsgId(), new String(message.getBody()));
        try {
            long now = System.currentTimeMillis();
            byte[] body = message.getBody();

            //返回的是一组qps信息
            HealthResult hr = new Gson().fromJson(new String(body), HealthResult.class);

            if (hr.getServiceInfoList().size() == 0) {
                return;
            }


            long envId = hr.getPipelineInfo().getEnvId();


            ProjectEnvDeploySetting ds = dao.fetch(ProjectEnvDeploySetting.class, Cnd.where("env_id", "=", envId));
            if (!Optional.ofNullable(ds).isPresent()) {
                log.warn("ProjectEnvDeploySetting is null:{}", envId);
                return;
            }

            ProjectPipeline pipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
            if (null == pipeline) {
                log.warn("pipeline is null env id: {}", envId);
                return;
            }

            //当前的副本数量
            int curReplicate = Optional.ofNullable(pipeline).map(p -> p.getDeployInfo()).map(d -> d.getDockerMachineList()).map(l -> l.size()).orElse(0);

            String labels = ds.getLabels();
            //在label中指定qps
            String value = LabelUtils.getLabelValue(labels, "qps");

            //没有qps的情况下,只支持健康监测
            if (!StringUtils.isEmpty(value)) {
                try {
                    expansionOrShrink(now, hr, envId, ds, curReplicate, value);
                } catch (Throwable ex) {
                    log.error("expansionOrShrink " + envId + " error:" + ex.getMessage(), ex);
                }
            }


            hr.getServiceInfoList().stream().forEach(info -> {
                MError error = new MError();
                error.setCtime(now);
                error.setUtime(now);
                error.setIp(info.getIp());

                ErrorContent content = new ErrorContent();
                content.setEnvId(hr.getPipelineInfo().getEnvId());
                content.setPipelineId(hr.getPipelineInfo().getPipelineId());
                content.setProjectId(hr.getPipelineInfo().getProjectId());
                content.setQps(info.getQps());
                content.setReplicate(curReplicate);
                error.setContent(content);


                //挂掉的服务器
                if (info.getStatus() != 0) {
                    //保存重启事件
                    restartEvent(now, envId, info, error);
                }
            });
            log.info("handleHealthCheck :{}", hr);
            //更新env 尽量避免修改其他数据(有竞态关系)
            dao.update("project_env", Chain.make("health_check_result", new Gson().toJson(hr)), Cnd.where("id", "=", envId));

        } catch (Throwable ex) {
            log.warn("health check error:" + ex.getMessage(), ex);
        }
    }

    /**
     * 计算每台机器qps之和.
     *
     * @param list 机器qps list
     * @return 返回qps之和
     */
    private long getTotalQPS(List<Long> list) {
        long total = 0;
        for (long qps : list) {
            total += qps;
        }
        return total;
    }

    /**
     * 判断能否缩容
     * 当前机器数量必须大于1
     * 减少一台机器以后,每台机器的平均qps要小于qpsLimit参数
     * 如果大于或等于qpsLimit就无需缩容.
     * 目的:避免缩容以后马上又扩容
     *
     * @param curReplicate 当前机器数量
     * @param qpsLimit
     * @param qpsList
     * @return
     */
    private boolean canScaleDown(int curReplicate, long qpsLimit, List<Long> qpsList) {
        double totalQPS = getTotalQPS(qpsList);
        return curReplicate - 1 > 0 && totalQPS / (curReplicate - 1) < qpsLimit;
    }

    private void expansionOrShrink(long now, HealthResult hr, long envId, ProjectEnvDeploySetting ds, int curReplicate, String value) {
        long qps = StringUtils.isNotEmpty(value) ? Long.valueOf(value) : 0L;
        if (qps <= 0L) {
            return;
        }

        long maxReplicate = ds.getMaxReplicate();
        long replicate = ds.getReplicate();


        List<ServiceInfo> expansionList = hr.getServiceInfoList().stream().filter(it -> it.getQps() >= qps && curReplicate < maxReplicate).collect(Collectors.toList());
        long expansionCount = expansionList.size();

        log.info("expansion envId:{} curReplicate:{} maxReplicate:{} replicate:{} expansionSize:{}", envId, curReplicate, maxReplicate, replicate, expansionList.size());


        List<Long> qpsList = hr.getServiceInfoList().stream().map(it -> it.getQps()).collect(Collectors.toList());

        //需要扩容 扩容优先级高
        if (0 != expansionCount && expansionCount >= curReplicate / 2) {
            MError error = new MError();
            long realQps = expansionList.stream().map(it -> it.getQps()).reduce((a, b) -> a + b).get() / expansionCount;
            saveHealthEvent(error, envId, now, realQps, qpsList, qps, ScaleType.expansion);
            return;
        }


        //扩容后1小时内不允许自动缩容
        if (gwCache.get(GwCache.HOUR, GwCache.expansionKey(envId)) != null) {
            return;
        }

        if (!canScaleDown(curReplicate, qps, qpsList)) {
            return;
        }

        //扩容和缩容不能同时进行
        List<ServiceInfo> shrinkList = hr.getServiceInfoList().stream().filter(it -> it.getQps() < qps && curReplicate > replicate).collect(Collectors.toList());
        long shrinkCount = shrinkList.size();

        log.info("Shrink envId:{} curReplicate:{} replicate:{} shrinkCount:{}", envId, curReplicate, replicate, shrinkList.size());
        //需要缩容
        if (0 != shrinkCount
            && shrinkCount > curReplicate / 2) {
            MError error = new MError();
            long realQps = shrinkList.stream().map(it -> it.getQps()).reduce((a, b) -> a + b).get() / shrinkCount;
            saveHealthEvent(error, envId, now, realQps, qpsList, qps, ScaleType.shrink);
        }
    }

    private void restartEvent(long now, long envId, ServiceInfo info, MError error) {
        String ip = info.getIp();
        //避免错误日志膨胀,也可以做时间维度的处理
        String key = Stream.of(ip, String.valueOf(envId)).collect(Collectors.joining("_"));
        MError merror = dao.fetch(MError.class, Cnd.where("key", "=", key)
            .and("type", "=", MError.ErrorType.HealthCheck.ordinal()));
        if (null == merror) {
            error.setType(MError.ErrorType.HealthCheck.ordinal());
            error.setKey(key);
            dao.insert(error);
        } else {
            //每次有一分钟的处理时间
            if (merror.getUtime() + TimeUnit.MINUTES.toMillis(1) < now) {
                merror.setUtime(now);
                merror.setStatus(0);
                merror.setServiceName(envId + ":重启");
                dao.update(merror);
            }
        }
    }


    private void saveHealthEvent(MError event, long envId, long now, long realQps, List<Long> qpsList, long qps, ScaleType type) {
        try {
            boolean l = lock.tryLock(2, TimeUnit.SECONDS);
            if (l) {
                try {
                    //qps负载过高的服务器,维度是envId
                    int eventType = type.equals(ScaleType.expansion) ? MError.ErrorType.HealthCheckLoadHigh.ordinal() : MError.ErrorType.HealthCheckLoadLow.ordinal();
                    String message = type.equals(ScaleType.expansion) ? ":自动扩容:" : ":自动缩容:";
                    MError merror = dao.fetch(MError.class, Cnd.where("key", "=", envId).and("type", "=", eventType));
                    if (null == merror) {
                        event.setCtime(now);
                        event.setUtime(now);
                        event.setServiceName(envId + message + realQps + ":" + qps);
                        event.setKey(String.valueOf(envId));
                        event.setType(eventType);
                        event.setContent(new ErrorContent());
                        event.getContent().setEnvId(envId);
                        event.getContent().setQpsList(qpsList);
                        dao.insert(event);
                    } else {
                        if (merror.getUtime() + TimeUnit.MINUTES.toMillis(1) < now) {
                            merror.setUtime(now);
                            merror.setStatus(0);
                            if (null == merror.getContent()) {
                                merror.setContent(new ErrorContent());
                            }
                            merror.getContent().setEnvId(envId);
                            merror.getContent().setQps(realQps);
                            merror.getContent().setQpsList(qpsList);
                            merror.setServiceName(envId + message + realQps + ":" + qps);
                            dao.update(merror);
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.warn("saveHealthEvent error:{}", e.getMessage());
        }
    }
}
