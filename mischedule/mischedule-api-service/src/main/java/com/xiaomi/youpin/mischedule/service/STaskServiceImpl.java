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

package com.xiaomi.youpin.mischedule.service;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.youpin.mischedule.MethodInfo;
import com.xiaomi.youpin.mischedule.annotation.Task;
import com.xiaomi.youpin.mischedule.api.service.STaskService;
import com.xiaomi.youpin.mischedule.api.service.bo.ExecuteType;
import com.xiaomi.youpin.mischedule.api.service.bo.STaskContext;
import com.xiaomi.youpin.mischedule.api.service.bo.STaskParam;
import com.xiaomi.youpin.mischedule.api.service.bo.STaskResult;
import com.xiaomi.youpin.mischedule.bo.HttpTaskParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class STaskServiceImpl implements STaskService {

    private static final String version = "0.0.2:2019-10-29";

    @Autowired
    private ApplicationContext ac;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private RegistryConfig registryConfig;

    private String serviceName = "com.xiaomi.youpin.mischedule.api.service.ScheduleService";


    @Value("${schedule.provider.group}")
    private String group;


    private String dubboVersion = "";

    private int timeOut = 2000;


    /**
     * 收集信息并且注册回来
     */
    @Override
    public void init() {
        log.info("STaskService init version:{}", version);

        try {
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            reference.setApplication(applicationConfig);
            reference.setRegistry(registryConfig);
            reference.setInterface(serviceName);
            reference.setGeneric(Constants.GENERIC_SERIALIZATION_YOUPIN_JSON);
            reference.setGroup(group);
            reference.setVersion(dubboVersion);
            reference.setTimeout(timeOut);
            RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, String.valueOf(timeOut));
            ReferenceConfigCache cache = ReferenceConfigCache.getCache();
            GenericService service = cache.get(reference);


            Map<String, Object> tasksMap = ac.getBeansWithAnnotation(Task.class);
            log.info("{}", tasksMap);

            List<com.xiaomi.youpin.mischedule.api.service.bo.Task> taskList = tasksMap.entrySet().stream().map(it -> {
                Task taskAnno = it.getValue().getClass().getAnnotation(Task.class);

                if (null == taskAnno) {
                    taskAnno = it.getValue().getClass().getSuperclass().getAnnotation(Task.class);
                }

                com.xiaomi.youpin.mischedule.api.service.bo.Task task = new com.xiaomi.youpin.mischedule.api.service.bo.Task();
                task.setName(taskAnno.source() + ":" + taskAnno.name());
                ExecuteType type = ExecuteType.valueOf(taskAnno.type());
                task.setExecuteType(type);
                task.setCron(taskAnno.cron());

                if (type.equals(ExecuteType.http)) {
                    HttpTaskParam param = new HttpTaskParam();
                    param.setUrl(taskAnno.path());
                    param.setMethodType(taskAnno.methodType());
                    param.setHeaders(Maps.newHashMap());
                    task.setParam(new Gson().toJson(param));
                }

                if (type.equals(ExecuteType.dubbo)) {
                    MethodInfo methodInfo = new MethodInfo();
                    methodInfo.setGroup(taskAnno.group());
                    methodInfo.setMethodName(taskAnno.methodName());
                    methodInfo.setVersion(taskAnno.version());
                    methodInfo.setServiceName(taskAnno.serviceName());
                    methodInfo.setParameterTypes(taskAnno.parameterType());
                    methodInfo.setArgs(taskAnno.args());
                    task.setParam(new Gson().toJson(methodInfo));
                }

                return task;
            }).collect(Collectors.toList());
            if (taskList.size() > 0) {
                Object res = service.$invoke("regTask", new String[]{"java.util.List"}, new Object[]{new Gson().toJson(taskList)});
                log.info("schedule reg task success:{}", res);
            }
        } catch (Throwable ex) {
            log.warn("schedule reg task error:{}", ex.getMessage());
        }
    }

    @Override
    public void close() {
        log.info("STaskService close");
    }


    @Override
    public STaskResult execute(STaskParam param, STaskContext context) {
        return new STaskResult();
    }
}
