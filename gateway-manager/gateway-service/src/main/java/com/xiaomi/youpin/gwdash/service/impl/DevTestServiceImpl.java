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
//package com.xiaomi.youpin.gwdash.service.impl;
//
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import com.dianping.cat.Cat;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.xiaomi.data.push.nacos.NacosNaming;
//import com.xiaomi.youpin.gwdash.bo.DubboTestBo;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.service.DevTestService;
//import com.xiaomi.youpin.mischedule.MethodInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.common.Constants;
//import org.apache.dubbo.config.ApplicationConfig;
//import org.apache.dubbo.config.ReferenceConfig;
//import org.apache.dubbo.config.RegistryConfig;
//import org.apache.dubbo.config.utils.ReferenceConfigCache;
//import org.apache.dubbo.rpc.RpcContext;
//import org.apache.dubbo.rpc.service.GenericException;
//import org.apache.dubbo.rpc.service.GenericService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author zhangjunyi
// * created on 2020/4/27 4:26 下午
// */
//@Service
//@Slf4j
//public class DevTestServiceImpl implements DevTestService {
//
//    @Autowired
//    private ApplicationConfig applicationConfig;
//
//    @Autowired
//    private RegistryConfig registryConfig;
//
//    @Autowired
//    private NacosNaming nacosNaming;
//
//    @Override
//    public Result<Object> excecuteDubbo(DubboTestBo dubboTestBo) {
//        log.info("excecuteDubbo:{}", new Gson().toJson(dubboTestBo));
//
//        MethodInfo methodInfo = new MethodInfo();
//        methodInfo.setGroup(dubboTestBo.getGroup());
//        methodInfo.setMethodName(dubboTestBo.getMethodName());
//        methodInfo.setServiceName(dubboTestBo.getServiceName());
//
//        if (StringUtils.isNotEmpty(dubboTestBo.getAddr())) {
//            methodInfo.setAddr(dubboTestBo.getAddr());
//        }
//        if (StringUtils.isNotEmpty(dubboTestBo.getIp())) {
//            methodInfo.setIp(dubboTestBo.getIp());
//        }
//        if (StringUtils.isNotEmpty(dubboTestBo.getVersion())) {
//            methodInfo.setVersion(dubboTestBo.getVersion());
//        }
//        if (StringUtils.isEmpty(dubboTestBo.getParamsType())) {
//            methodInfo.setParameterTypes(new String[]{});
//        } else {
//            String[] types = new Gson().fromJson(dubboTestBo.getParamsType(), new TypeToken<String[]>(){}.getType());
//            methodInfo.setParameterTypes(types);
//        }
//
//        Object[] params = new Gson().fromJson(dubboTestBo.getParams(), new TypeToken<Object[]>(){}.getType());
//        if (params != null) {
//            methodInfo.setArgs(params);
//        } else {
//            methodInfo.setArgs(new Object[]{});
//        }
//
//        methodInfo.setAddr(dubboTestBo.getAddr());
//        return call(methodInfo);
//    }
//
//    private Result<Object> call(MethodInfo methodInfo) {
//        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
//        reference.setApplication(applicationConfig);
//        reference.setRegistry(registryConfig);
//        reference.setInterface(methodInfo.getServiceName());
//        reference.setGeneric(true);
//        reference.setCheck(false);
//        reference.setGroup(methodInfo.getGroup());
//        reference.setVersion(methodInfo.getVersion());
//        reference.setTimeout(methodInfo.getTimeout());
//        RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, String.valueOf(methodInfo.getTimeout()));
//        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
//        GenericService genericService = cache.get(reference);
//
//        /**
//         * 定向调用
//         */
//        if (StringUtils.isNotEmpty(methodInfo.getAddr())) {
//            String[] ss = methodInfo.getAddr().split(":");
//            RpcContext.getContext().setAttachment(Constants.MUST_PROVIDER_IP_PORT, "true");
//            RpcContext.getContext().setAttachment(Constants.PROVIDER_IP, ss[0]);
//            if (ss.length > 1) {
//                RpcContext.getContext().setAttachment(Constants.PROVIDER_PORT, ss[1]);
//            }
//        }
//
//        try {
//            return Result.success(genericService.$invoke(methodInfo.getMethodName(), methodInfo.getParameterTypes(), methodInfo.getArgs())) ;
//        } catch (Exception e) {
//            return Result.fail(1,e.getMessage());
//        } finally {
//            RpcContext.getContext().clearAttachments();
//            RpcContext.getContext().remove(Constants.TRACE_ID);
//        }
//    }
//
//    @Override
//    public Map<String, Instance> dubboInstanceMap(String serviceName, String group, String version) {
//        HashMap<String, Instance> map = new HashMap<>();
//        if (StringUtils.isEmpty(serviceName)) {
//            return map;
//        }
//        serviceName = "providers:" + serviceName;
//        if (StringUtils.isNotEmpty(version)) {
//            serviceName = serviceName + ":" + version;
//        }
//        if (StringUtils.isNotEmpty(group)) {
//            serviceName = serviceName + ":" + group;
//        }
//
//        try {
//            List<Instance> instances = nacosNaming.getAllInstances(serviceName);
//            for (Instance instance : instances) {
//                if (instance.isEnabled() && instance.isHealthy()) {
//                    map.put(instance.getIp(), instance);
//                }
//            }
//        } catch (NacosException e) {
//            log.error("ipInstanceMap error", e);
//            Cat.logError(e);
//        }
//        return map;
//    }
//}