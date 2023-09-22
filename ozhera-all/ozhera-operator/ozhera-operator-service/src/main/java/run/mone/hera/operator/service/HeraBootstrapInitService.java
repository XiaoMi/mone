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
package run.mone.hera.operator.service;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Service;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
import io.fabric8.kubernetes.api.model.LoadBalancerStatus;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.ibatis.jdbc.ScriptRunner;
import run.mone.hera.operator.bo.HeraBootstrap;
import run.mone.hera.operator.bo.HeraObjectMeta;
import run.mone.hera.operator.bo.HeraResource;
import run.mone.hera.operator.bo.HeraSpec;
import run.mone.hera.operator.bo.HeraStatus;
import run.mone.hera.operator.bo.PropConf;
import run.mone.hera.operator.common.FileUtils;
import run.mone.hera.operator.common.HoConstant;
import run.mone.hera.operator.common.K8sUtilBean;
import run.mone.hera.operator.common.ResourceTypeEnum;
import run.mone.hera.operator.dto.DeployStateDTO;
import run.mone.hera.operator.dto.HeraOperatorDefineDTO;
import run.mone.hera.operator.dto.PodStateDTO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Initialize the mone family bucket namespace.
 *
 * @author shanwb
 * @date 2022-06-17
 */
@Service
@Slf4j
public class HeraBootstrapInitService {

    private Gson gson = new Gson();

    @javax.annotation.Resource
    private KubernetesClient kubernetesClient;

    @javax.annotation.Resource(name = "deploymentClient")
    private MixedOperation<Deployment, DeploymentList, Resource<Deployment>> deploymentClient;

    @javax.annotation.Resource
    private K8sUtilBean k8sUtilBean;

    private volatile HeraOperatorDefineDTO heraOperatorDefine = new HeraOperatorDefineDTO();

    public void init() {
        try {
            HeraSpec heraSpec = new HeraSpec();
            HeraObjectMeta heraMeta = new HeraObjectMeta();
            heraMeta.setName(HoConstant.HERA_CR_NAME);
            heraMeta.setNamespace(HoConstant.HERA_NAMESPACE);

            List<HeraResource> resourceList = new ArrayList<>();
            heraSpec.setResourceList(resourceList);

            HeraResource mysql = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/mysql/ozhera_mysql.yaml")
                    .resourceType(ResourceTypeEnum.MYSQL.getTypeName())
                    .resourceName("hera-mysql")
                    .remark("local pv, Make sure the disk directory has been created in advance.")
                    .build();
            mysql.setDefaultYaml();
            List<Map<String, String>> mysqlConnectionMapList = new ArrayList<>();
            mysqlConnectionMapList.add(kvMap(HoConstant.KEY_DATASOURCE_URL, "mone-db-all:3306", "mysql address，host:port"));
            mysqlConnectionMapList.add(kvMap(HoConstant.KEY_DATASOURCE_USERNAME, "root", "user name"));
            mysqlConnectionMapList.add(kvMap(HoConstant.KEY_DATASOURCE_PASSWORD, "Mone_123456", "password"));
            mysql.setConnectionMapList(mysqlConnectionMapList);
            resourceList.add(mysql);

            HeraResource redis = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/redis/ozhera_redis.yaml")
                    .resourceType(ResourceTypeEnum.REDIS.getTypeName())
                    .resourceName("hera-redis")
                    .remark("local redis.")
                    .build();
            redis.setDefaultYaml();
            List<Map<String, String>> redisConnectionMapList = new ArrayList<>();
            redisConnectionMapList.add(kvMap(HoConstant.KEY_REDIS_URL, "redis-service:6379", "redis address, host:port"));
            redisConnectionMapList.add(kvMap(HoConstant.KEY_REDIS_PASSWORD, "", "redis pwd, optional", "0"));
            redis.setConnectionMapList(redisConnectionMapList);
            resourceList.add(redis);

            HeraResource es = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/es/ozhera_es.yaml")
                    .resourceType(ResourceTypeEnum.ES.getTypeName())
                    .resourceName("hera-es")
                    .remark("local es")
                    .build();
            es.setDefaultYaml();
            List<Map<String, String>> esConnectionMapList = new ArrayList<>();
            esConnectionMapList.add(kvMap(HoConstant.KEY_ES_URL, "elasticsearch:9200", "es address, host:port"));
            esConnectionMapList.add(kvMap(HoConstant.KEY_ES_USERNAME, "", "es user name, optional", "0"));
            esConnectionMapList.add(kvMap(HoConstant.KEY_ES_PASSWORD, "", "es password, optional", "0"));
            es.setConnectionMapList(esConnectionMapList);
            resourceList.add(es);

            HeraResource rocketMQ = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/rocketmq/ozhera_rocketmq.yaml")
                    .resourceType(ResourceTypeEnum.ROCKETMQ.getTypeName())
                    .resourceName("hera-rocketMQ")
                    .remark("local rocketMQ")
                    .build();
            rocketMQ.setDefaultYaml();
            List<Map<String, String>> rocketMQConnectionMapList = new ArrayList<>();
            rocketMQConnectionMapList.add(kvMap(HoConstant.KEY_ROCKETMQ_NAMESERVER, "rocketmq-name-server-service:9876", "rocketMQ address，host:port"));
            rocketMQConnectionMapList.add(kvMap(HoConstant.KEY_ROCKETMQ_AK, "", "rocketMQ accessKey，optional", "0"));
            rocketMQConnectionMapList.add(kvMap(HoConstant.KEY_ROCKETMQ_SK, "", "rocketMQ secretKey，optional", "0"));
            rocketMQ.setConnectionMapList(rocketMQConnectionMapList);
            resourceList.add(rocketMQ);

            HeraResource nacos = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/nacos/ozhera_nacos.yml")
                    .resourceType(ResourceTypeEnum.Nacos.getTypeName())
                    .resourceName("hera-nacos")
                    .remark("load nacos")
                    .defaultExtendConfigPath(new String[]{
                            "/ozhera_init/nacos/config/hera_app_config_#_DEFAULT_GROUP.properties",
                            "/ozhera_init/nacos/config/hera_log_manager_open_#_DEFAULT_GROUP.properties",
                            "/ozhera_init/nacos/config/hera_trace_config_#_DEFAULT_GROUP.properties",
                            "/ozhera_init/nacos/config/log_stream_dataId_open_#_DEFAULT_GROUP.properties",
                            "/ozhera_init/nacos/config/mi_tpc_#_DEFAULT_GROUP.properties",
                            "/ozhera_init/nacos/config/mi_tpc_login_#_DEFAULT_GROUP.properties",
                            "/ozhera_init/nacos/config/mimonitor_open_config_#_DEFAULT_GROUP.properties",
                            "/ozhera_init/nacos/config/prometheus_agent_open_config_#_DEFAULT_GROUP.properties"})
                    .build();

            nacos.setDefaultYaml();
            nacos.setDefaultExtendConfig();

            List<Map<String, String>> connectionMapList = new ArrayList<>();
            connectionMapList.add(kvMap(HoConstant.KEY_NACOS_ADDRESS, "nacos:80", "nacos address, host:ip"));
            connectionMapList.add(kvMap(HoConstant.KEY_NACOS_USERNAME, "nacos", "nacos user name, optional", "0"));
            connectionMapList.add(kvMap(HoConstant.KEY_NACOS_PASSWORD, "nacos", "nacos password, optional", "0"));
            nacos.setConnectionMapList(connectionMapList);

            resourceList.add(nacos);

            HeraResource tpcLoginFe = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/tpc-login-fe/ozhera_tpc_login_fe.yml")
                    .resourceType(ResourceTypeEnum.HERA_FE.getTypeName())
                    .resourceName("hera-tpc-login-fe")
                    .remark("load tpc-login-fe")
                    .build();
            tpcLoginFe.setDefaultYaml();
            List<Map<String, String>> tpcLoginFeConnectionMapList = new ArrayList<>();
            tpcLoginFe.setConnectionMapList(tpcLoginFeConnectionMapList);
            resourceList.add(tpcLoginFe);

            HeraResource grafana = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/grafana/ozhera_grafana.yaml")
                    .resourceType(ResourceTypeEnum.GRAFANA.getTypeName())
                    .resourceName("hera-grafana")
                    .remark("local pv, Make sure the disk directory has been created in advance.")
                    .build();
            grafana.setDefaultYaml();
            List<Map<String, String>> grafanaConnectionMapList = new ArrayList<>();
            grafana.setConnectionMapList(grafanaConnectionMapList);
            resourceList.add(grafana);

            HeraResource prometheus = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/prometheus/ozhera_prometheus.yaml")
                    .resourceType(ResourceTypeEnum.PROMETHEUS.getTypeName())
                    .resourceName("hera-prometheus")
                    .remark("local pv, Make sure the disk directory has been created in advance.")
                    .build();
            prometheus.setDefaultYaml();
            List<Map<String, String>> prometheusConnectionMapList = new ArrayList<>();
            prometheus.setConnectionMapList(prometheusConnectionMapList);
            resourceList.add(prometheus);

            HeraResource heraFe = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/hera-fe/ozhera_fe.yml")
                    .resourceType(ResourceTypeEnum.HERA_FE.getTypeName())
                    .resourceName("hera-fe")
                    .remark("load hera-fe")
                    .build();
            heraFe.setDefaultYaml();
            List<Map<String, String>> heraFeConnectionMapList = new ArrayList<>();
            heraFe.setConnectionMapList(heraFeConnectionMapList);
            resourceList.add(heraFe);

            HeraResource alertManager = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/alertManager/ozhera_alertmanager.yaml")
                    .resourceType(ResourceTypeEnum.ALERT_MANAGER.getTypeName())
                    .resourceName("hera-alertmanager")
                    .remark("local pv, Make sure the disk directory has been created in advance.")
                    .build();
            alertManager.setDefaultYaml();
            resourceList.add(alertManager);

            HeraResource tpcLogin = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/tpc-login/ozhera_tpc_login.yml")
                    .resourceType(ResourceTypeEnum.HERA_APP.getTypeName())
                    .resourceName("hera-tpc-login")
                    .remark("load tpc-login")
                    .build();
            tpcLogin.setDefaultYaml();
            resourceList.add(tpcLogin);


            HeraResource traceEtlEs = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/trace-etl-es/ozhera_trace_etl_es.yml")
                    .resourceType(ResourceTypeEnum.HERA_APP.getTypeName())
                    .resourceName("trace-etl-es")
                    .remark("load trace-etl-es")
                    .build();
            traceEtlEs.setDefaultYaml();
            resourceList.add(traceEtlEs);

            HeraResource cadvisor = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/cadvisor/ozhera_cadvisor.yaml")
                    .resourceType(ResourceTypeEnum.CADVISOR.getTypeName())
                    .resourceName("hera-cadvisor")
                    .remark("local pv, Make sure the disk directory has been created in advance.")
                    .build();
            cadvisor.setDefaultYaml();
            List<Map<String, String>> cadvisorConnectionMapList = new ArrayList<>();
            cadvisor.setConnectionMapList(cadvisorConnectionMapList);
            resourceList.add(cadvisor);

            HeraResource node_exporter = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/node-exporter/ozhera_node-exporter.yaml")
                    .resourceType(ResourceTypeEnum.NODE_EXPORTER.getTypeName())
                    .resourceName("hera-node-exporter")
                    .remark("local pv, Make sure the disk directory has been created in advance.")
                    .build();
            node_exporter.setDefaultYaml();
            List<Map<String, String>> node_exporterConnectionMapList = new ArrayList<>();
            node_exporter.setConnectionMapList(node_exporterConnectionMapList);
            resourceList.add(node_exporter);

            HeraResource heraApp = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/hera-app/ozhera_app.yml")
                    .resourceType(ResourceTypeEnum.HERA_APP.getTypeName())
                    .resourceName("hera-app")
                    .remark("load hera-app")
                    .build();
            heraApp.setDefaultYaml();
            resourceList.add(heraApp);

            HeraResource logManager = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/log-manager/ozhera_log_manager.yml")
                    .resourceType(ResourceTypeEnum.HERA_APP.getTypeName())
                    .resourceName("hera-log-manager")
                    .remark("load log-manager")
                    .build();
            logManager.setDefaultYaml();
            resourceList.add(logManager);

            HeraResource logAgentServer = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/log-agent-server/ozhera_log_agent-server.yml")
                    .resourceType(ResourceTypeEnum.HERA_APP.getTypeName())
                    .resourceName("hera-log-agent-server")
                    .remark("load log-agent-server")
                    .build();
            logAgentServer.setDefaultYaml();
            resourceList.add(logAgentServer);

            HeraResource logStream = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/log-stream/ozhera_log_stream.yml")
                    .resourceType(ResourceTypeEnum.HERA_APP.getTypeName())
                    .resourceName("hera-log-stream")
                    .remark("load log-stream")
                    .build();
            logStream.setDefaultYaml();
            resourceList.add(logStream);

            HeraResource mimonitor = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/mimonitor/ozhera_mimonitor.yml")
                    .resourceType(ResourceTypeEnum.HERA_APP.getTypeName())
                    .resourceName("hera-mimonitor")
                    .remark("load mimonitor")
                    .build();
            mimonitor.setDefaultYaml();
            resourceList.add(mimonitor);

            HeraResource tpc = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/tpc/ozhera_tpc.yml")
                    .resourceType(ResourceTypeEnum.HERA_APP.getTypeName())
                    .resourceName("hera-tpc")
                    .remark("load tpc")
                    .build();
            tpc.setDefaultYaml();
            resourceList.add(tpc);

            HeraResource tpcFe = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/tpc-fe/ozhera_tpc_fe.yml")
                    .resourceType(ResourceTypeEnum.HERA_FE.getTypeName())
                    .resourceName("hera-tpc-fe")
                    .remark("load tpc-fe")
                    .build();
            tpcFe.setDefaultYaml();
            resourceList.add(tpcFe);

            HeraResource traceEtlManager = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/trace-etl-manager/ozhera_trace_etl_manager.yml")
                    .resourceType(ResourceTypeEnum.HERA_APP.getTypeName())
                    .resourceName("hera-trace_etl_manager")
                    .remark("load trace-etl-manager")
                    .build();
            traceEtlManager.setDefaultYaml();
            resourceList.add(traceEtlManager);

            HeraResource traceEtlServer = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/trace-etl-server/ozhera_trace_etl_server.yml")
                    .resourceType(ResourceTypeEnum.HERA_APP.getTypeName())
                    .resourceName("hera-trace-etl-server")
                    .remark("load trace-etl-server")
                    .build();
            traceEtlServer.setDefaultYaml();
            resourceList.add(traceEtlServer);

            HeraResource heraWebhook = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/webhook/ozhera_webhook_server.yaml")
                    .resourceType(ResourceTypeEnum.HERA_WEBHOOK.getTypeName())
                    .resourceName("hera-webhook")
                    .remark("load hera-webhook")
                    .build();
            heraWebhook.setDefaultYaml();
            resourceList.add(heraWebhook);

            HeraResource demoServer = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/demo/ozhera_demo_server.yml")
                    .resourceType(ResourceTypeEnum.OTHER.getTypeName())
                    .resourceName("hera-demo-server")
                    .remark("load hera-demo-server")
                    .build();
            demoServer.setDefaultYaml();
            resourceList.add(demoServer);

            HeraResource demoClient = HeraResource.builder()
                    .needCreate(true)
                    .required(true)
                    .defaultYamlPath("/ozhera_init/demo/ozhera_demo_client.yml")
                    .resourceType(ResourceTypeEnum.OTHER.getTypeName())
                    .resourceName("hera-demo-client")
                    .remark("load hera-demo-client")
                    .build();
            demoClient.setDefaultYaml();
            resourceList.add(demoClient);

            //todo other resource
            heraOperatorDefine.setHeraSpec(heraSpec);
            heraOperatorDefine.setHeraMeta(heraMeta);

            log.warn("##heraOperatorDefine load success##");
        } catch (Exception e) {
            log.error("HeraOperatorController init error:", e);
            throw new RuntimeException("HeraOperatorController init error");
        }
    }

    public HeraOperatorDefineDTO getResource() {
        return heraOperatorDefine;
    }

    public synchronized HeraOperatorDefineDTO updateResource(HeraOperatorDefineDTO heraOperatorDefine) {
        preCheck(heraOperatorDefine);
        replacePlaceholder(heraOperatorDefine);

        this.heraOperatorDefine = heraOperatorDefine;

        return this.heraOperatorDefine;
    }

    private void replacePlaceholder(HeraOperatorDefineDTO heraOperatorDefine) {
        HeraSpec heraSpec = heraOperatorDefine.getHeraSpec();

        Map<String, String> params = new HashMap<>();
        for (HeraResource heraResource : heraSpec.getResourceList()) {
            if (null != heraResource.getConnectionMapList()) {
                for (Map<String, String> m : heraResource.getConnectionMapList()) {
                    params.put(m.get("key"), m.get("value"));
                }
            }

            if (ResourceTypeEnum.Nacos.getTypeName().equals(heraResource.getResourceType())) {
                List<PropConf> propList = heraResource.getPropList();
                if (null != propList) {
                    StringSubstitutor sub = new StringSubstitutor(params);
                    for (PropConf propConf : propList) {
                        String properties = propConf.getValue();
                        String replacedString = sub.replace(properties);

                        propConf.setValue(replacedString);
                    }
                }
            }
        }
    }

    private void preCheck(HeraOperatorDefineDTO heraOperatorDefine) {
        com.google.common.base.Preconditions.checkArgument(null != heraOperatorDefine.getHeraMeta(), "heraMeta can not be null");
        com.google.common.base.Preconditions.checkArgument(null != heraOperatorDefine.getHeraMeta().getName());
        com.google.common.base.Preconditions.checkArgument(null != heraOperatorDefine.getHeraMeta().getNamespace());

        com.google.common.base.Preconditions.checkArgument(null != heraOperatorDefine.getHeraSpec());
        com.google.common.base.Preconditions.checkArgument(null != heraOperatorDefine.getHeraSpec().getResourceList());

        List<HeraResource> resourceList = heraOperatorDefine.getHeraSpec().getResourceList();
        Set<String> resourceNameSet = new HashSet<>();
        for (HeraResource hr : resourceList) {
            String resourceName = hr.getResourceName();
            com.google.common.base.Preconditions.checkArgument(StringUtils.isNotBlank(resourceName));
            if (resourceNameSet.contains(resourceName)) {
                throw new IllegalArgumentException(String.format("resourceName:%s must be unique", resourceName));
            }
            resourceNameSet.add(resourceName);

            if (null != hr.getNeedCreate() && hr.getNeedCreate()) {
                Preconditions.checkArgument(StringUtils.isNotBlank(hr.getYamlStr()), String.format("resource:%s yaml content can not be empty", resourceName));
            }
        }
    }


    public List<DeployStateDTO> crState() {
        List<DeployStateDTO> result = new ArrayList<>();
        String namespace = heraOperatorDefine.getHeraMeta().getNamespace();


        DeploymentList deploymentList = deploymentClient.inNamespace(namespace).list();
        List<Deployment> list = deploymentList.getItems();
        if (CollectionUtils.isEmpty(list)) {
            new ArrayList<>();
        }

        for (Deployment deployment : list) {
            DeployStateDTO deployStateDTO = new DeployStateDTO();
            boolean ready = true;
            String deploymentName = deployment.getMetadata().getName();
            ;
            Integer replicas = deployment.getStatus().getReplicas();
            Integer readyReplicas = deployment.getStatus().getReadyReplicas();
            if (replicas != readyReplicas) {
                ready = false;
            }

            deployStateDTO.setReady(ready);
            deployStateDTO.setDeploymentName(deploymentName);
            deployStateDTO.setReadyState((null == readyReplicas ? 0 : readyReplicas) + "/" + replicas);
            deployStateDTO.setAge(deployment.getMetadata().getCreationTimestamp());

            result.add(deployStateDTO);

        }
        return result;
    }

    public List<PodStateDTO> crState1() {
        List<PodStateDTO> podStateDTOList = new ArrayList<>();
        String namespace = heraOperatorDefine.getHeraMeta().getNamespace();

        PodList podList = kubernetesClient.pods().inNamespace(namespace).list();
        for (Pod pod : podList.getItems()) {

            List<ContainerStatus> containerStatusList = pod.getStatus().getContainerStatuses();
            if (CollectionUtils.isEmpty(containerStatusList)) {
                continue;
            }

            containerStatusList.stream().forEach(c -> {
                PodStateDTO podStateDTO = new PodStateDTO();
                podStateDTO.setPodName(pod.getMetadata().getName());
                podStateDTO.setNamespace(pod.getMetadata().getNamespace());

                podStateDTO.setImage(c.getImage());
                podStateDTO.setContainerID(c.getContainerID());
                podStateDTO.setReady(c.getReady());
                podStateDTO.setStarted(c.getStarted());
                podStateDTO.setLastState(c.getLastState());
                podStateDTO.setState(c.getState());

                podStateDTOList.add(podStateDTO);
            });
        }

        log.warn("hera operator status:{}", gson.toJson(podStateDTOList));
        return podStateDTOList;

    }

    public boolean crExists(String namespace) {
        MixedOperation<HeraBootstrap, KubernetesResourceList<HeraBootstrap>, Resource<HeraBootstrap>> heraMixedOperation = kubernetesClient.resources(HeraBootstrap.class);
        List<HeraBootstrap> crList = heraMixedOperation.list().getItems();

        return CollectionUtils.isNotEmpty(crList);
    }

    public HeraStatus createOrReplaceCr() {
        HeraBootstrap heraBootstrap = new HeraBootstrap();
        HeraStatus heraStatus = new HeraStatus();
        ObjectMeta objectMeta = new ObjectMeta();

        String namespace = heraOperatorDefine.getHeraMeta().getNamespace();
        MixedOperation<HeraBootstrap, KubernetesResourceList<HeraBootstrap>, Resource<HeraBootstrap>> heraMixedOperation = kubernetesClient.resources(HeraBootstrap.class);

        HeraBootstrap result;
        List<HeraBootstrap> crList = heraMixedOperation.list().getItems();
        if (CollectionUtils.isEmpty(crList)) {
            objectMeta.setNamespace(namespace);
            objectMeta.setName(heraOperatorDefine.getHeraMeta().getName());
            heraBootstrap.setSpec(heraOperatorDefine.getHeraSpec());
            heraBootstrap.setStatus(heraStatus);
            heraBootstrap.setMetadata(objectMeta);

            result = heraMixedOperation.inNamespace(namespace).create(heraBootstrap);
        } else {
            HeraBootstrap exists = crList.get(0);
            exists.setSpec(heraOperatorDefine.getHeraSpec());
            exists.setStatus(heraStatus);
            result = heraMixedOperation.inNamespace(namespace).replace(exists);
        }

        log.warn("hera operator result:{}", gson.toJson(result));

        return result.getStatus();
    }

    public Boolean deleteCr() {
        HeraBootstrap heraBootstrap = new HeraBootstrap();
        HeraStatus heraStatus = new HeraStatus();
        ObjectMeta objectMeta = new ObjectMeta();

        String namespace = heraOperatorDefine.getHeraMeta().getNamespace();
        objectMeta.setNamespace(namespace);
        objectMeta.setName(heraOperatorDefine.getHeraMeta().getName());

        heraBootstrap.setSpec(heraOperatorDefine.getHeraSpec());
        heraBootstrap.setStatus(heraStatus);
        heraBootstrap.setMetadata(objectMeta);
        MixedOperation<HeraBootstrap, KubernetesResourceList<HeraBootstrap>, Resource<HeraBootstrap>> heraMixedOperation = kubernetesClient.resources(HeraBootstrap.class);
        Boolean result = heraMixedOperation.inNamespace(namespace).delete(heraBootstrap);
        log.warn("hera operator delete result:{}", result);

        return result;
    }


    public void deleteService(List<String> serviceNameList, String namespace, String serviceType) {
        List<io.fabric8.kubernetes.api.model.Service> serviceList = listService(serviceNameList, namespace, serviceType);
        if (CollectionUtils.isNotEmpty(serviceList)) {
            kubernetesClient.services().inNamespace(namespace).delete(serviceList);
        }
    }

    public List<io.fabric8.kubernetes.api.model.Service> createAndListService(List<String> serviceNameList, String namespace, String yamlPath, String serviceType) throws InterruptedException {
        // Filter service by name
        List<io.fabric8.kubernetes.api.model.Service> serviceList = listService(serviceNameList, namespace);
        if (CollectionUtils.isEmpty(serviceList)) {
            String yaml = FileUtils.readResourceFile(yamlPath);
            k8sUtilBean.applyYaml(yaml, namespace, "add");
        }

        TimeUnit.SECONDS.sleep(2);
        // Filter service by name + type.
        return listService(serviceNameList, namespace, serviceType);
    }

    public boolean checkLbServiceFailed(List<io.fabric8.kubernetes.api.model.Service> serviceList, String serviceType) {
        if (CollectionUtils.isEmpty(serviceList)) {
            return true;
        }

        Map<String, String> ipPortMap = getServiceIpPort(serviceList, serviceType);
        if (ipPortMap.size() == serviceList.size()) {
            return false;
        }

        for (io.fabric8.kubernetes.api.model.Service s : serviceList) {
            String timestamp = s.getMetadata().getCreationTimestamp();

            Long seconds = diffSeconds(timestamp);
            // 37 seconds have not yet been determined as a failed service creation.
            if (seconds > 37) {
                log.warn("serviceType:{} create failed : cost too many time:{}s", serviceType, seconds);
                return true;
            }
        }

        return false;
    }

    private long diffSeconds(String timestamp) {
        Instant instant = Instant.parse(timestamp);
        ZonedDateTime utcDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
        ZonedDateTime beijingDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        long diffSeconds = currentDateTime.toEpochSecond() - beijingDateTime.toEpochSecond();

        return diffSeconds;
    }


    private List<io.fabric8.kubernetes.api.model.Service> listService(List<String> serviceNameList, String namespace) {
        ServiceList serviceList = kubernetesClient.services().inNamespace(namespace).list();
        return serviceList.getItems().stream()
                .filter(s -> serviceNameList.contains(s.getMetadata().getName()))
                .collect(Collectors.toList());
    }

    private List<io.fabric8.kubernetes.api.model.Service> listService(List<String> serviceNameList, String namespace, String serviceType) {
        ServiceList serviceList = kubernetesClient.services().inNamespace(namespace).list();
        return serviceList.getItems().stream()
                .filter(s -> serviceNameList.contains(s.getMetadata().getName()))
                .filter(s -> serviceType.equalsIgnoreCase(s.getSpec().getType()))
                .collect(Collectors.toList());
    }

    public Map<String, String> getServiceIpPort(List<io.fabric8.kubernetes.api.model.Service> serviceList, String serviceType) {
        Map<String, String> ipPortMap = new HashMap<>();
        String nodePortIP = null;
        if ("NodePort".equals(serviceType)) {
            List<Node> nodeList = kubernetesClient.nodes().list().getItems();
            Optional<NodeAddress> nodeAddress = nodeList.get(0).getStatus().getAddresses().stream()
                    .filter(address -> "InternalIP".equals(address.getType())).findAny();
            if (!nodeAddress.isPresent()) {
                throw new RuntimeException("cluster node have no internalIP");
            }
            nodePortIP = nodeAddress.get().getAddress();
            if (StringUtils.isBlank(nodePortIP)) {
                throw new RuntimeException("cluster node get null [NodePort] IP");
            }
        }

        for (io.fabric8.kubernetes.api.model.Service service : serviceList) {
            String serviceName = service.getMetadata().getName();
            ServiceSpec serviceSpec = service.getSpec();
            String type = serviceSpec.getType();
            if ("NodePort".equals(type)) {
                Integer nodePort = serviceSpec.getPorts().get(0).getNodePort();
                ipPortMap.put(serviceName, nodePortIP + ":" + nodePort);
            } else if ("LoadBalancer".equals(type)) {
                LoadBalancerStatus lsStatus = service.getStatus().getLoadBalancer();
                List<LoadBalancerIngress> lbIngress = lsStatus.getIngress();
                if (CollectionUtils.isNotEmpty(lbIngress)) {
                    //todo The port is temporarily hardcoded.
                    ipPortMap.put(serviceName, lbIngress.get(0).getIp() + ":80");
                }
            }
        }

        return ipPortMap;
    }

    public String getServiceType(List<io.fabric8.kubernetes.api.model.Service> serviceList) {
        for (io.fabric8.kubernetes.api.model.Service service : serviceList) {
            ServiceSpec serviceSpec = service.getSpec();
            String type = serviceSpec.getType();
            return type;
        }
        return "";
    }

    private Map<String, String> kvMap(String key, String value, String remark) {
        return this.kvMap(key, value, remark, "1");
    }

    private Map<String, String> kvMap(String key, String value, String remark, String required) {
        Map<String, String> map = new HashMap<>();
        map.put("key", key);
        map.put("value", value);
        map.put("remark", remark);
        map.put("required", required);
        return map;
    }

    public void executeSqlScript(File[] sqlFiles, String url, String userName, String pwd) {
        Connection con = null;
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            //Getting the connection
            con = DriverManager.getConnection(url, userName, pwd);
            System.out.println("Connection established......");

            for (File sqlFile : sqlFiles) {
                //Initialize the script runner
                ScriptRunner sr = new ScriptRunner(con);
                //Creating a reader object
                Reader reader = new BufferedReader(new FileReader(sqlFile));
                //Running the script
                sr.runScript(reader);
                reader.close();
            }


        } catch (Exception e) {
            log.error("sql execute error", e);
        } finally {
            if (null != con) {
                try {
                    con.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }
}
