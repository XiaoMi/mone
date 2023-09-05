/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.hera.operator.controller;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.ExceptionHelper;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import run.mone.hera.operator.bo.HeraBootstrap;
import run.mone.hera.operator.bo.HeraResource;
import run.mone.hera.operator.bo.HeraStatus;
import run.mone.hera.operator.common.HoConstant;
import run.mone.hera.operator.common.ResourceTypeEnum;
import run.mone.hera.operator.dto.DeployStateDTO;
import run.mone.hera.operator.dto.HeraOperatorDefineDTO;
import run.mone.hera.operator.dto.OperatorStateDTO;
import run.mone.hera.operator.dto.ServiceCheckResource;
import run.mone.hera.operator.service.HeraBootstrapInitService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;


/**
 * @author shanwb
 * @date 2023-01-31
 */
@Slf4j
@Controller
public class OzHeraOperatorController {

    private Gson gson = new Gson();

    private Semaphore semaphore = new Semaphore(1);

    @javax.annotation.Resource
    private HeraBootstrapInitService heraBootstrapInitService;

    @RequestMapping(path = "/hera/operator/resource/get", method = "get", timeout = 5000L)
    public Result<HeraOperatorDefineDTO> getResource() {
        log.info("/hera/operator/resource/get call in");
        HeraOperatorDefineDTO heraOperatorDefineDTO = heraBootstrapInitService.getResource();
        heraOperatorDefineDTO.setCrExists(heraBootstrapInitService.crExists(heraOperatorDefineDTO.getHeraMeta().getNamespace()));

        return Result.success(heraOperatorDefineDTO);
    }


    @RequestMapping(path = "/hera/operator/service/createAndCheck", method = "get", timeout = 5000L)
    public Result<ServiceCheckResource> createAndCheckHeraService(@RequestParam("namespace") String namespace) {
        try {
            log.info("/hera/operator/service/createAndCheck call in");
            Preconditions.checkArgument(null != namespace);
            ServiceCheckResource serviceCheckResource = new ServiceCheckResource();
            serviceCheckResource.setStatus(1);

            Map<String, String> serviceMap = new HashMap<>();
            serviceMap.put("hera-nginx", HoConstant.KEY_HERA_URL);
            serviceMap.put("tpclogin-nginx", HoConstant.KEY_TPC_LOGIN_FE_URL);
            serviceMap.put("tpc-nginx", HoConstant.KEY_HERA_TPC_URL);
            serviceMap.put("grafana", HoConstant.KEY_GRAFANA_URL);
            serviceMap.put("alertmanager", HoConstant.KEY_ALERTMANAGER_URL);
            serviceMap.put("prometheus", HoConstant.KEY_PROMETHEUS_URL);

            List<String> serviceNameList = new ArrayList<>((serviceMap.keySet()));

            String serviceType = "LoadBalancer";
            String serviceYamlPath = "/ozhera_init/outer/ozhera_lb.yml";
            List<io.fabric8.kubernetes.api.model.Service> serviceList = heraBootstrapInitService.createAndListService(serviceNameList, namespace, serviceYamlPath, serviceType);
            if (heraBootstrapInitService.checkLbServiceFailed(serviceList, serviceType)) {
                if (CollectionUtils.isNotEmpty(serviceList)) {
                    log.warn("LoadBalancer type failed, change to NodePort type");
                    heraBootstrapInitService.deleteService(serviceNameList, namespace, serviceType);
                }

                serviceType = "NodePort";
                serviceYamlPath = "/ozhera_init/outer/ozhera_nodeport.yml";
                serviceList = heraBootstrapInitService.createAndListService(serviceNameList, namespace, serviceYamlPath, serviceType);
            } else {
                String currentType = heraBootstrapInitService.getServiceType(serviceList);
                if ("NodePort".equals(currentType)) {
                    log.warn("NodePort type finish");
                    serviceType = "NodePort";
                }
            }

            Map<String, String> ipPortMap = heraBootstrapInitService.getServiceIpPort(serviceList, serviceType);

            serviceCheckResource.setServiceType(serviceType);
            if (ipPortMap.size() == serviceNameList.size()) {
                serviceCheckResource.setStatus(0);

                HeraResource heraResource = HeraResource.builder()
                        .needCreate(false)
                        .required(true)
                        .resourceType(ResourceTypeEnum.SERVICE_CHECK.getTypeName())
                        .resourceName("k8s-serviceType")
                        .remark("k8s service方式获取")
                        .build();
                List<Map<String, String>> connectionMapList = new ArrayList<>();
                for (Map.Entry<String, String> entry : ipPortMap.entrySet()) {
                    connectionMapList.add(kvMap(serviceMap.get(entry.getKey()), entry.getValue(), "对外访问地址"));
                }
                heraResource.setConnectionMapList(connectionMapList);

                serviceCheckResource.setHeraResource(heraResource);
            }

            return Result.success(serviceCheckResource);
        } catch (Throwable e) {
            log.error("createAndCheckHeraService error", e);
            return Result.fromException(e);
        }
    }



    private Map<String, String> kvMap(String key, String value, String remark) {
        Map<String, String> map = new HashMap<>();
        map.put("key", key);
        map.put("value", value);
        map.put("remark", remark);
        return map;
    }



    @RequestMapping(path = "/hera/operator/resource/update", method = "post", timeout = 5000L)
    public Result<HeraOperatorDefineDTO> updateResource(HeraOperatorDefineDTO heraOperatorDefine) {
        try {
            HeraOperatorDefineDTO newDefineDTo = heraBootstrapInitService.updateResource(heraOperatorDefine);
            return Result.success(newDefineDTo);
        } catch (Throwable e) {
            log.error("updateResource error", e);
            return Result.fromException(e);
        }
    }

    @RequestMapping(path = "/hera/operator/cr/createOrReplace", timeout = 5000L)
    public Result<HeraStatus> createCustomResource() {
        if (semaphore.tryAcquire()) {
            try {
                HeraStatus status = heraBootstrapInitService.createOrReplaceCr();
                log.warn("hera operator result:{}", gson.toJson(status));
                return Result.success(status);
            } catch (Throwable e) {
                log.error("createCustomResource failed:", e);
                return Result.success(new HeraStatus());
            } finally {
                semaphore.release();
            }
        } else {
            return Result.fromException(new RuntimeException("only support one serial request，please wait and try again"));
        }
    }

    @RequestMapping(path = "/hera/operator/cr/delete", timeout = 5000L)
    public Result<Boolean> deleteCustomResource() {
        HeraBootstrap heraBootstrap = new HeraBootstrap();
        HeraStatus heraStatus = new HeraStatus();
        ObjectMeta objectMeta = new ObjectMeta();

        if (semaphore.tryAcquire()) {
            try {
                Boolean result = heraBootstrapInitService.deleteCr();
                log.warn("hera operator delete result:{}", result);

                return Result.success(result);
            } catch (Throwable e) {
                log.error("createCustomResource failed:", e);
                return Result.success(false);
            } finally {
                semaphore.release();
            }
        } else {
            return Result.fromException(new RuntimeException("only support one serial request，please wait and try again"));
        }
    }

    @RequestMapping(path = "/hera/operator/cr/state", timeout = 5000L)
    public Result<OperatorStateDTO> customResourceState() {
        try {
            OperatorStateDTO operatorState = new OperatorStateDTO();
            List<DeployStateDTO> deployStateDTOList = heraBootstrapInitService.crState();
            operatorState.setDeployStateList(deployStateDTOList);
            log.warn("hera operator status:{}", gson.toJson(deployStateDTOList));

            Optional<DeployStateDTO> optional = deployStateDTOList.stream().filter(d -> !d.getReady()).findAny();
            if (optional.isPresent() || deployStateDTOList.size() < 19) {
                operatorState.setStatus(1);
            } else {
                operatorState.setStatus(0);
            }

            return Result.success(operatorState);
        } catch (Throwable e) {
            log.error("customResourceState failed:", e);
            return Result.fromException(ExceptionHelper.create(GeneralCodes.InternalError, e.getMessage()));
        }
    }

}
