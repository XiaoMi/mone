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
package run.mone.hera.operator.handler;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.xiaomi.data.push.client.HttpClientV6;
import com.xiaomi.youpin.docean.anno.Component;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import run.mone.hera.operator.bo.*;
import run.mone.hera.operator.common.HoConstant;
import run.mone.hera.operator.common.K8sUtilBean;
import run.mone.hera.operator.common.ResourceTypeEnum;
import run.mone.hera.operator.service.ESService;
import run.mone.hera.operator.service.RocketMQSerivce;

import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wenbang
 */
@Slf4j
@Component(name = "HeraResourceEventHandler")
@Data
@NoArgsConstructor
public class HeraResourceEventHandler implements ResourceEventHandler<HeraBootstrap> {

    @javax.annotation.Resource
    private KubernetesClient kubernetesClient;

    @javax.annotation.Resource(name = "heraClient")
    private MixedOperation<HeraBootstrap, HeraBootstrapList, Resource<HeraBootstrap>> heraClient;

    @javax.annotation.Resource(name = "deploymentClient")
    private MixedOperation<Deployment, DeploymentList, Resource<Deployment>> deploymentClient;

    @javax.annotation.Resource(name = "serviceClient")
    private MixedOperation<Service, ServiceList, Resource<Service>> serviceClient;

    @javax.annotation.Resource(name = "pvClient")
    private MixedOperation<PersistentVolumeClaim, PersistentVolumeClaimList, Resource<PersistentVolumeClaim>> pvClient;

    @javax.annotation.Resource
    private ESService esService;

    @javax.annotation.Resource
    private RocketMQSerivce rocketMQSerivce;

    @javax.annotation.Resource
    private K8sUtilBean k8sUtilBean;

    private Gson gson = new Gson();

    @Override
    public void onAdd(HeraBootstrap heraBootstrap) {
        try {
            Preconditions.checkArgument(null != heraBootstrap);
            Preconditions.checkArgument(null != heraBootstrap.getSpec());
            Preconditions.checkArgument(null != heraBootstrap.getMetadata());

            log.info("### on add hera{}", heraBootstrap);
            if (null != heraBootstrap.getStatus() && HeraStatus.STATUS_SUCCESS == heraBootstrap.getStatus().getStatus()) {
                log.warn("### HERA platform initialized, no need to init again");
                return;
            }

            HeraSpec heraSpec = heraBootstrap.getSpec();
            ObjectMeta objectMeta = heraBootstrap.getMetadata();
            List<HeraResource> heraResourceList = heraSpec.getResourceList();

            //0. Group by resource order
            TreeMap<Integer, List<HeraResource>> groupHrList = heraResourceList.stream()
                    .collect(Collectors.groupingBy(h ->
                                    ResourceTypeEnum.typeOf(h.getResourceType()).getOrder(), TreeMap::new, Collectors.toList()
                            )
                    );

            int step = 10;
            for (Map.Entry<Integer, List<HeraResource>> entrySet : groupHrList.entrySet()) {
                List<HeraResource> hrList = entrySet.getValue();
                log.warn("hera operator add, applyResource resourceType:{}, HeraResource size:{}", entrySet.getKey(), hrList.size());
                //1. deploy
                for (HeraResource heraResource : hrList) {
                    k8sUtilBean.applyResource(heraResource, objectMeta, "add");
                }

                //2. Checking deployment status
                TimeUnit.SECONDS.sleep(step--);
                block2checkStatus(objectMeta);

                //3. Resource initialization
                for (HeraResource heraResource : hrList) {
                    // nacos configuration initialization
                    if (ResourceTypeEnum.Nacos.getTypeName().equals(heraResource.getResourceType())) {
                        Preconditions.checkArgument(null != heraResource.getConnectionMapList(), "nacos connection kv config can not be null");

                        Map<String, String> newMap = new HashMap<>();
                        heraResource.getConnectionMapList().forEach(map -> newMap.put(map.get("key"), map.get("value")));

                        Preconditions.checkArgument(StringUtils.isNotEmpty(newMap.get(HoConstant.KEY_NACOS_ADDRESS)), String.format("nacos connection config:%s can not be null", HoConstant.KEY_NACOS_ADDRESS));
                        Preconditions.checkArgument(StringUtils.isNotEmpty(newMap.get(HoConstant.KEY_NACOS_PASSWORD)), String.format("nacos connection config:%s can not be null", HoConstant.KEY_NACOS_PASSWORD));
                        // nacos initialization
                        initNacos("add", newMap.get(HoConstant.KEY_NACOS_ADDRESS), newMap.get(HoConstant.KEY_NACOS_PASSWORD), heraResource.getPropList());
                    }
                    // es configuration initialization
                    if (ResourceTypeEnum.ES.getTypeName().equals(heraResource.getResourceType())) {
                        Preconditions.checkArgument(null != heraResource.getConnectionMapList(), "es connection kv config can not be null");

                        Map<String, String> newMap = new HashMap<>();
                        heraResource.getConnectionMapList().forEach(map -> newMap.put(map.get("key"), map.get("value")));

                        Preconditions.checkArgument(StringUtils.isNotEmpty(newMap.get(HoConstant.KEY_ES_URL)), String.format("es connection config:%s can not be null", HoConstant.KEY_ES_URL));

                        initES(newMap.get(HoConstant.KEY_ES_URL), newMap.get(HoConstant.KEY_ES_USERNAME), newMap.get(HoConstant.KEY_ES_PASSWORD));
                    }
                    // mysql configuration initialization
                    if (ResourceTypeEnum.MYSQL.getTypeName().equals(heraResource.getResourceType())) {
                        Preconditions.checkArgument(null != heraResource.getConnectionMapList(), "mysql connection kv config can not be null");

                        Map<String, String> newMap = new HashMap<>();
                        heraResource.getConnectionMapList().forEach(map -> newMap.put(map.get("key"), map.get("value")));

                        Preconditions.checkArgument(StringUtils.isNotEmpty(newMap.get(HoConstant.KEY_DATASOURCE_URL)), String.format("mysql connection config:%s can not be null", HoConstant.KEY_DATASOURCE_URL));
                        Preconditions.checkArgument(StringUtils.isNotEmpty(newMap.get(HoConstant.KEY_DATASOURCE_USERNAME)), String.format("es connection config:%s can not be null", HoConstant.KEY_DATASOURCE_USERNAME));
                        Preconditions.checkArgument(StringUtils.isNotEmpty(newMap.get(HoConstant.KEY_DATASOURCE_PASSWORD)), String.format("es connection config:%s can not be null", HoConstant.KEY_DATASOURCE_PASSWORD));

                        initSql("add", newMap.get(HoConstant.KEY_DATASOURCE_URL), newMap.get(HoConstant.KEY_DATASOURCE_USERNAME), newMap.get(HoConstant.KEY_DATASOURCE_PASSWORD));
                    }
                    // rocketmq configuration initialization
                    if (ResourceTypeEnum.ROCKETMQ.getTypeName().equals(heraResource.getResourceType())) {
                        Preconditions.checkArgument(null != heraResource.getConnectionMapList(), "rocketmq connection kv config can not be null");

                        Map<String, String> newMap = new HashMap<>();
                        heraResource.getConnectionMapList().forEach(map -> newMap.put(map.get("key"), map.get("value")));

                        Preconditions.checkArgument(StringUtils.isNotEmpty(newMap.get(HoConstant.KEY_ROCKETMQ_NAMESERVER)), String.format("rocketmq nameserver config:%s can not be null", HoConstant.KEY_ROCKETMQ_NAMESERVER));

                        initRocketMQ(newMap.get(HoConstant.KEY_ROCKETMQ_NAMESERVER));
                    }
                }
            }

            HeraStatus heraStatus = new HeraStatus();
            heraStatus.setStatus(HeraStatus.STATUS_SUCCESS);
            heraStatus.setMsg("success");
            heraBootstrap.setStatus(heraStatus);
            heraClient.patchStatus(heraBootstrap);
            log.warn("hera operator onAdd success");
        } catch (Throwable e) {
            log.error("hera operator onAdd error:", e);
            HeraStatus heraStatus = new HeraStatus();
            heraStatus.setStatus(HeraStatus.STATUS_FAILED);
            heraStatus.setMsg(ExceptionUtils.getStackTrace(e));
            heraBootstrap.setStatus(heraStatus);
            heraClient.patchStatus(heraBootstrap);
        }
    }


    private void block2checkStatus(ObjectMeta objectMeta) throws InterruptedException {
        String namespace = objectMeta.getNamespace();

        for (int i = 0; i < 100; i++) {
            boolean ready = true;
            DeploymentList deploymentList = deploymentClient.inNamespace(namespace).list();
            List<Deployment> list = deploymentList.getItems();
            if (CollectionUtils.isEmpty(list)) {
                return;
            }

            for (Deployment deployment : list) {
                String deploymentName = deployment.getMetadata().getName();
                Integer replicas = deployment.getStatus().getReplicas();
                Integer readyReplicas = deployment.getStatus().getReadyReplicas();
                if (replicas != readyReplicas) {
                    ready = false;
                    log.warn("deployment:{} not ready, getReplicas:{}, ReadyReplicas:{}", deploymentName, replicas, readyReplicas);
                }
            }

            if (!ready) {
                TimeUnit.SECONDS.sleep(i + 2);
            } else {
                TimeUnit.SECONDS.sleep(15);
                return;
            }
        }

        throw new RuntimeException("deployment not ready, after retry 15 times, break");
    }

//    private void initResource(String action) {
//        // sql 脚本初始化
//        initSql(action);
//        // nacos集群、配置初始化
//        initNacos(action);
//    }

//    private void initApp(String action) {
//        // tpc根节点初始化
//        initTpc(action);
//    }

    private void initTpc(String action) {
        String url = "http://tpc-mone-b2c-srv:8097/tpc/init";
        int retryTimes = 9;
        Connection con = null;
        while (retryTimes-- > 0) {
            try {
                String init = HttpClientV6.get(url, new HashMap<>(), 2000);
                log.error("tpc init result:{}", init);
                if (init.contains("ConnectException")) {
                    throw new RuntimeException(init);
                }
                break;
            } catch (Exception e) {
                log.error("tpc init error retryTimes:{}", retryTimes, e);
                try {
                    TimeUnit.SECONDS.sleep(17);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void initNacos(String action, String nacosAddress, String pwd, List<PropConf> propConfList) {
        log.warn("initNacos begin nacosAddress:{}", nacosAddress);
        String url = String.format("http://%s/nacos/v1/ns/cluster/enable?level=4&pwd=%s", nacosAddress, pwd);
        String nacosEnable = HttpClientV6.get(url, new HashMap<>(), 2000);
        if (!"ok".equals(nacosEnable)) {
            log.error("nacos cluster enable failed:{}", nacosEnable);
        } else {
            log.error("nacos cluster enable success");
        }

        String nacosCfApi = String.format("http://%s/nacos/v1/cs/configs", nacosAddress);

        for (PropConf propConf : propConfList) {
            int retryTimes = 3;
            Connection con = null;
            while (retryTimes-- > 0) {
                try {
                    String content = propConf.getValue();
                    String eContent = URLEncoder.encode(content, "UTF-8");
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    headers.put("charset", "utf-8");

                    String key = propConf.getKey();
                    String dataId = key.substring(0, key.indexOf("_#_"));
                    String group = key.substring(key.indexOf("_#_") + 3);
                    String body = String.format("type=%s&dataId=%s&group=%s&content=%s", "properties", dataId, group, eContent);
                    log.warn("create nacos conig file:{}", body);
                    String result = HttpClientV6.post(nacosCfApi, body, headers, 3000);
                    if (!"true".equals(result)) {
                        log.error("create nacos conig file failed:{}", result);
                    } else {
                        log.error("create nacos conig file success");
                    }

                    break;
                } catch (IOException e) {
                    log.error("nacos init error retryTimes:{}", retryTimes, e);
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    private void initSql(String action, String url, String userName, String pwd) {
        //mysql安装完后，初始化数据
        String[] scripts = new String[]{"/ozhera_init/mysql/sql/nacos.sql",
                "/ozhera_init/mysql/sql/tpc.sql",
                "/ozhera_init/mysql/sql/grafana.sql",
                "/ozhera_init/mysql/sql/hera.sql"};
        log.warn("sql scripts:{}", scripts);
        executeSqlScript(scripts, url, userName, pwd);
        log.warn("sql scripts execte success");
    }

    private void executeSqlScript(String[] scripts, String url, String userName, String pwd) {
        int retryTimes = 3;
        Connection con = null;
        while (retryTimes-- > 0) {
            try {
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                //Getting the connection
                String mysqlUrl = String.format("jdbc:mysql://%s/?useUnicode=true&characterEncoding=utf8&useSSL=false&connectTimeout=4000&socketTimeout=60000", url);
                con = DriverManager.getConnection(mysqlUrl, userName, pwd);
                log.warn("Connection established :{}", mysqlUrl);

                for (String sqlFile : scripts) {
                    //Initialize the script runner
                    ScriptRunner sr = new ScriptRunner(con);
                    //Creating a reader object
                    Reader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(sqlFile)));
                    //Running the script
                    sr.runScript(reader);
                    reader.close();
                }
                break;
            } catch (CommunicationsException e) {
                log.error("CommunicationsException:{}, retryTimes:{}", e.getMessage(), retryTimes);
                try {
                    TimeUnit.SECONDS.sleep(15);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                log.error("sql execute error", e);
                break;
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

    private void initES(String url, String userName, String pwd) {
        esService.createESTemplate(url, userName, pwd);
    }

    private void initRocketMQ(String nameserver){
        rocketMQSerivce.createTopic(nameserver);
    }

    @Override
    public void onUpdate(HeraBootstrap oldObj, HeraBootstrap newObj) {
        //log.info("update mone:{}", newObj);

        log.info("####onUpdate hera xxx");
    }

    @Override
    public void onDelete(HeraBootstrap heraBootstrap, boolean deletedFinalStateUnknown) {
        log.info("delete mone:{}", heraBootstrap.getSpec());

        try {
            log.info("### on delete hera{}", heraBootstrap);

            HeraSpec heraSpec = heraBootstrap.getSpec();
            ObjectMeta objectMeta = heraBootstrap.getMetadata();
            List<HeraResource> heraResourceList = heraSpec.getResourceList();
            for (HeraResource heraResource : heraResourceList) {
                try {
                    k8sUtilBean.applyResource(heraResource, objectMeta, "delete");
                } catch (Exception e) {
                    log.warn("heraResource:{} delete error", heraResource.getResourceName() , e);
                }
            }
        } catch (Throwable e) {
            log.error("hera operator onAdd error:", e);
        }
    }
}
