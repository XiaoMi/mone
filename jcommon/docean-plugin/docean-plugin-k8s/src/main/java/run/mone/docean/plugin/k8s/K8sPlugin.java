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

package run.mone.docean.plugin.k8s;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.Pair;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetList;
import io.fabric8.kubernetes.api.model.autoscaling.v1.HorizontalPodAutoscalerList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.fabric8.kubernetes.api.model.autoscaling.v1.HorizontalPodAutoscaler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/24 20:31
 */
@DOceanPlugin
@Slf4j
public class K8sPlugin implements IPlugin {

    private KubernetesClient client;
    private static final KubernetesClient DEFAULT_CLIENT;

    static {
        DEFAULT_CLIENT = new DefaultKubernetesClient();
    }

    /**
     * custom resources definitions
     */
    private List<Crd> crdList = new ArrayList<>();

    private Map<String, Class> rdMap = new HashMap<>();

    private Map<String, Pair<Class, Class>> clientMap = new HashMap<>();

    public K8sPlugin() {
        clientMap.put("podClient", new Pair<>(Pod.class, PodList.class));
        clientMap.put("nodeClient", new Pair<>(Node.class, NodeList.class));
        clientMap.put("deploymentClient", new Pair<>(Deployment.class, DeploymentList.class));
        clientMap.put("serviceClient", new Pair<>(Service.class, ServiceList.class));
        clientMap.put("configMapClient", new Pair<>(ConfigMap.class, ConfigMapList.class));
        clientMap.put("jobClient", new Pair<>(Job.class, JobList.class));
        clientMap.put("nsClient", new Pair<>(Namespace.class, NamespaceList.class));
        clientMap.put("pvClient", new Pair<>(PersistentVolume.class, PersistentVolumeList.class));
        clientMap.put("HPAClient", new Pair<>(HorizontalPodAutoscaler.class, HorizontalPodAutoscalerList.class));
        clientMap.put("replicasetClient", new Pair<>(ReplicaSet.class, ReplicaSetList.class));

        rdMap.put("nodeResourceEventHandler", Node.class);
        rdMap.put("deploymentResourceEventHandler", Deployment.class);
        rdMap.put("serviceResourceEventHandler", Service.class);
        rdMap.put("podResourceEventHandler", Pod.class);
        rdMap.put("nsResourceEventHandler", Namespace.class);
        rdMap.put("jobResourceEventHandler", Job.class);
        rdMap.put("HPAResourceEventHandler", HorizontalPodAutoscaler.class);
        rdMap.put("replicasetResourceEventHandler", ReplicaSet.class);
    }

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init k8s plugin");
        client = new DefaultKubernetesClient();
        ioc.putBean(KubernetesClient.class.getName(), client);
        Config config = ioc.getBean(Config.class);
        String customResourceStr = config.get("crd", "");
        // custom resources
        if (StringUtils.isNotEmpty(customResourceStr)) {
            crdList = Arrays.stream(customResourceStr.split(";")).map(it -> {
                String[] array = it.split(":");
                Crd crd = new Crd();
                crd.setCrdName(array[0]);
                crd.setResourceType(array[1]);
                crd.setListClass(array[2]);
                crd.setClientName(array[3]);
                crd.setHandlerName(array[4]);
                return crd;
            }).collect(Collectors.toList());
            //crd=mone:run.mone.bootstrap.operator.bo.MoneBootstrap:run.mone.bootstrap.operator.bo.MoneList:moneClient:moneResourceEventHandler
            crdList.stream().forEach(it -> {
                MixedOperation moneClient = client.resources(ReflectUtils.classForName(it.getResourceType()), ReflectUtils.classForName(it.getListClass()));
                ioc.putBean(it.getClientName(), moneClient);
            });
        }
        clientMap.entrySet().forEach(entry -> {
            MixedOperation c = client.resources(entry.getValue().getKey(), entry.getValue().getValue());
            ioc.putBean(entry.getKey(), c);
        });
    }


    @SneakyThrows
    @Override
    public boolean start(Ioc ioc) {
        SharedInformerFactory informerFactory = client.informers();

        rdMap.entrySet().forEach(entry -> {
            ResourceEventHandler nodeResourceEventHandler = ioc.getBean(entry.getKey(), null);
            Optional.ofNullable(nodeResourceEventHandler).ifPresent(it -> {
                SharedIndexInformer sharedIndexInformer = informerFactory.sharedIndexInformerFor(entry.getValue(), TimeUnit.MINUTES.toMillis(10));
                sharedIndexInformer.addEventHandler(it);
            });
        });

        crdList.stream().forEach(crd -> {
            ResourceEventHandler handler = ioc.getBean(crd.getHandlerName(), null);
            Optional.ofNullable(handler).ifPresent(it -> {
                SharedIndexInformer sharedIndexInformer = informerFactory.sharedIndexInformerFor(ReflectUtils.classForName(crd.getResourceType()), TimeUnit.MINUTES.toMillis(10));
                sharedIndexInformer.addEventHandler(it);
            });
        });


        Future<Void> startedInformersFuture = informerFactory.startAllRegisteredInformers();
        startedInformersFuture.get();
        informerFactory.addSharedInformerEventListener(exception -> log.info("Exception occurred, but caught", exception));
        return true;
    }

    @SneakyThrows
    private static Pair<Integer, String> execContainer(KubernetesClient client, String ns, String name, String container, String[] command) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();
        try (ExecWatch execWatch = client.pods().inNamespace(ns).withName(name)
                .inContainer(container)
                .writingOutput(out)
                .writingError(error)
                .exec(command)) {
            // 等待命令执行完成
            execWatch.close();
        }
        // 返回执行结果
        String result = out.toString();
        System.out.println("result:" + result);
        // 这里没有直接的退出值，你可能需要根据输出结果来判断命令是否成功执行
        return Pair.of(0, result);

    }

    @SneakyThrows
    public static Pair<Integer, String> KillContainer(String ns, String name, String container) {
        String[] shs = new String[]{"bash", "ash", "sh"};
        Pair<Integer, String> end = null;
        for (String sh : shs) {
            end = execContainer(DEFAULT_CLIENT, ns, name, container, new String[]{sh, "-c", "trap \"exit\" SIGINT SIGTERM ; kill -s SIGINT 1"});
            if (end.getKey().equals(0)) {
                break;
            }
        }
        return end;
    }
}
