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

package com.xiaomi.youpin.mischedule.task;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.gson.Gson;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import com.xiaomi.data.push.schedule.task.impl.http.HttpTask;
import com.xiaomi.data.push.schedule.task.impl.http.HttpTaskParam;
import com.xiaomi.youpin.mischedule.MethodInfo;
import com.xiaomi.youpin.mischedule.api.service.bo.*;
import com.xiaomi.youpin.mischedule.service.FeiShuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author gaoyibo
 * <p>
 * modify goodjava@qq.com
 * <p>
 * 健康监测
 * <p>
 * http 直接调用
 * dubbo 的去nacos上验证
 */
@Slf4j
@Component
public class HealthyTask extends AbstractTask {


    @Autowired
    private HttpTask httpTask;

    @Autowired
    private DubboTask dubboTask;


    @Autowired
    private NacosNaming nacosNaming;


    private static final String TAG = "HealthChecker";
    private static final String NUKE_TAG = "NukeTag";

    private static final String NOTIFY = "notify";

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private ProtocolConfig protocolConfig;

    @Autowired
    private RegistryConfig registryConfig;

    @Autowired
    private FeiShuService feiShuService;


    @Override
    public TaskResult execute(TaskParam taskParam, TaskContext taskContext) {
        log.info("health check:{}", new Gson().toJson(taskParam));

        int taskId = taskParam.getTaskId();

        HealthParam hp = new Gson().fromJson(taskParam.get(TaskParam.PARAM), HealthParam.class);

        List<HealthCheckUrl> urls = hp.getUrls().stream().map(it -> {
            HealthCheckUrl url = new HealthCheckUrl();
            if (isHttp(it)) {
                url.setType("http");
                url.setUrl(it);
                url.setIp(it.split("://|:")[1]);
            } else if (isDubbo(it)) {
                String[] ss = it.split("://|/");
                MethodInfo mi = new MethodInfo();
                mi.setIp(ss[1]);
                mi.setServiceName(ss[2]);
                mi.setGroup(ss[3]);
                mi.setMethodName(ss[4]);
                url.setMethodInfo(mi);
                url.setType("dubbo");
                url.setIp(ss[1]);
            }
            return url;
        }).collect(Collectors.toList());

        PipelineInfo pipelineInfo = hp.getPipelineInfo();


        HealthResult result = new HealthResult();
        result.setPipelineInfo(pipelineInfo);


        List<ServiceInfo> resList = urls.stream().map(it -> {
            if (it.getType().equals(HealthCheckUrl.HTTP)) {
                TaskParam tp = new TaskParam();
                HttpTaskParam p = new HttpTaskParam();
                p.setMethodType("get");
                p.setUrl(it.getUrl());
                tp.put(TaskParam.PARAM, new Gson().toJson(p));
                TaskResult res = httpTask.execute(tp, new TaskContext());
                ServiceInfo si = new ServiceInfo();
                si.setType("http");
                si.setStatus(res.getCode() == TaskResult.Success().getCode() ? 0 : 1);
                si.setIp(it.getIp());
                return si;
            } else if (it.getType().equals(HealthCheckUrl.DUBBO)) {
                MethodInfo mi = it.getMethodInfo();
                Pair<Integer, Long> pair = getDubboServiceStatus(mi.getServiceName(), mi.getGroup(), mi.getIp(), taskId, taskContext);
                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setServiceName(mi.getServiceName());
                serviceInfo.setGroup(mi.getGroup());
                serviceInfo.setType("dubbo");
                serviceInfo.setIp(it.getIp());
                serviceInfo.setStatus(pair.getKey());
                serviceInfo.setQps(pair.getValue());
                return serviceInfo;
            }
            return null;
        }).filter(it -> null != it).collect(Collectors.toList());

        result.setServiceInfoList(resList);

        taskContext.notifyMsg(TAG, new Gson().toJson(result));

        //清除不再适用的docker容器
        if (resList.size() > 0 && resList.get(0).getType().equals("dubbo")) {
            Set<String> ipSet = resList.stream().map(it -> it.getIp()).collect(Collectors.toSet());

            String serviceName = resList.get(0).getServiceName();

            serviceName = "providers:" + serviceName;
            if (StringUtils.isNotEmpty(resList.get(0).getGroup())) {
                serviceName = serviceName + ":" + resList.get(0).getGroup();
            }

//            nukeService(ipSet, serviceName, pipelineInfo, taskContext);
        }

        return TaskResult.Success();

    }

    private boolean isDubbo(String it) {
        return it.startsWith("dubbo");
    }

    private boolean isHttp(String it) {
        return it.startsWith("http");
    }


    private void nukeService(Set<String> ipSet, String serviceName, PipelineInfo pipelineInfo, TaskContext taskContext) {
        try {
            HealthResult result = new HealthResult();
            result.setPipelineInfo(pipelineInfo);
            List<Instance> instance = nacosNaming.getAllInstances(serviceName);
            List<ServiceInfo> list = instance.stream().filter(it -> {
                if (it.isEnabled() && it.isHealthy() && !ipSet.contains(it.getIp())) {
                    return true;
                }
                return false;
            }).map(it -> {
                ServiceInfo info = new ServiceInfo();
                info.setIp(it.getIp());
                return info;
            }).collect(Collectors.toList());
            result.setServiceInfoList(list);
            taskContext.notifyMsg(NUKE_TAG, new Gson().toJson(result));
        } catch (Throwable e) {
            log.error("error:{}", e.getMessage());
        }
    }

    /**
     * 0 正常  1 有问题
     *
     * @param serviceName
     * @param group
     * @param ip
     * @param taskContext
     * @return
     */
    private Pair<Integer, Long> getDubboServiceStatus(String serviceName, String group, String ip, int taskId, TaskContext taskContext) {
        long now = System.currentTimeMillis();
        serviceName = "providers:" + serviceName;
        if (StringUtils.isNotEmpty(group)) {
            serviceName = serviceName + ":" + group;
        }
        try {
            List<Instance> instance = nacosNaming.getAllInstances(serviceName);
            Optional<Instance> optional = instance.stream().filter(it -> it.getIp().equals(ip) && it.isEnabled() && it.isHealthy()).findFirst();

            if (optional.isPresent()) {
                try {
                    String interfaceName = optional.get().getMetadata().get("interface");
                    Map data = getDubboServiceStatusByGeneric(optional.get().getIp(), optional.get().getPort(), interfaceName
                            , optional.get().getMetadata().get("group"), "health");
                    log.info("data:{}", data);
                    //标准的接口实现必须返回qps
                    if (null != data && data.containsKey("qps")) {
                        return Pair.of(0, (long) data.get("qps"));
                    }

                } catch (Throwable ex) {
                    log.error("error:{}", ex.getMessage());
                    sendMsg(taskContext, now, taskId, serviceName, ip, ex.getMessage());
                }
            } else {
                sendMsg(taskContext, now, taskId, serviceName, ip, "");
            }
            //如果没有这个ip没有服务,那么是有问题的
            return optional.isPresent() ? Pair.of(0, 0L) : Pair.of(1, 0L);
        } catch (NacosException e) {
            log.warn("getDubboServiceStatus error:{}", e.getMessage());
        }
        return Pair.of(0, 0L);
    }


    private void sendMsg(TaskContext taskContext, long now, int taskId, String serviceName, String ip, String error) {
        if (checkNotifyTime(taskContext, now)) {
            taskContext.put(NOTIFY, String.valueOf(now + TimeUnit.MINUTES.toMillis(1)));
            feiShuService.sendMsg("", "health check|" + taskId + "|" + serviceName + "|" + ip + "|error|" + error);
        }
    }


    private boolean checkNotifyTime(TaskContext context, long now) {
        String time = context.get(NOTIFY);
        if (StringUtils.isEmpty(time)) {
            return true;
        }

        long t = Long.valueOf(time);
        if (now - t >= 0) {
            return true;
        }

        return false;
    }


    /**
     * 获取服务的健康情况
     * 方法是固定的health  参数固定为空
     *
     * @param ip
     * @param port
     * @param interfaceName
     * @param group
     * @param methodName
     * @return
     */
    public Map getDubboServiceStatusByGeneric(String ip, int port, String interfaceName, String group, String methodName) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        reference.setRegistry(registryConfig);
        reference.setInterface(interfaceName);
        reference.setGeneric(true);
        reference.setGroup(group);
        reference.setTimeout(1000);
        RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, "1000");
        RpcContext.getContext().setAttachment(Constants.PROVIDER_IP, ip);
        RpcContext.getContext().setAttachment(Constants.PROVIDER_PORT, String.valueOf(port));
        RpcContext.getContext().setAttachment(Constants.MUST_PROVIDER_IP_PORT, "true");
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);

        String[] parameterTypes = new String[]{};

        Object res = genericService.$invoke(methodName, parameterTypes, new Object[]{});

        log.info("res:{}", res);

        Map m = (Map) res;
        Map data = (Map) m.get("data");
        return data;
    }

}
