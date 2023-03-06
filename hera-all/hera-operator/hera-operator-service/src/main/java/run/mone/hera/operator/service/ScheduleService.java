//package run.mone.bootstrap.operator.service;
//
//import com.google.common.collect.ImmutableMap;
//import com.xiaomi.youpin.docean.anno.Service;
//import com.xiaomi.youpin.docean.common.Safe;
//import io.fabric8.kubernetes.api.model.*;
//import io.fabric8.kubernetes.api.model.apps.Deployment;
//import io.fabric8.kubernetes.api.model.apps.DeploymentList;
//import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
//import io.fabric8.kubernetes.client.KubernetesClient;
//import io.fabric8.kubernetes.client.dsl.MixedOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.yaml.snakeyaml.Yaml;
//
//import javax.annotation.Resource;
//import java.io.InputStream;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * @author wenbang
// * @date 2022/6/7 15:09
// */
//@Service
//@Slf4j
//public class ScheduleService {
//
//    @Resource(name = "podClient")
//    private MixedOperation<Pod, PodList, io.fabric8.kubernetes.client.dsl.Resource<Pod>> podClient;
//
//    @Resource(name = "configMapClient")
//    private MixedOperation<ConfigMap, ConfigMapList, io.fabric8.kubernetes.client.dsl.Resource<ConfigMap>> configMapClient;
//
//    @Resource
//    private KubernetesClient kubernetesClient;
//
//    @Resource(name = "deploymentClient")
//    private MixedOperation<Deployment, DeploymentList, io.fabric8.kubernetes.client.dsl.Resource<Deployment>> deploymentClient;
//
//    private Random random = new Random(System.currentTimeMillis());
//
//    public void init() {
//        log.info("init");
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//            Safe.runAndLog(() -> {
//                log.info("execute");
////                upload();
////                download("app", "mysql", "/tmp/v", "/tmp/vv");
////                exec("app", "mysql", new String[]{"sh", "-c", "mysql -uroot -p123456 -e 'create database vvv'"});
////                deploymentStats();
////                createConfigMap();
////                patchDeployment();
////                containerNames();
//                //sidecar();
//            });
//        }, 5, 100000, TimeUnit.SECONDS);
//
//    }
//
//    /**
//     * 执行命令(在指定pod中)
//     * 使用方法比较原始,但也适用性最广
//     */
//    public void exec(String labelKey, String labelValue, String[] cmd) {
//        podClient.withLabel(labelKey, labelValue).list().getItems().forEach(it -> {
//            String name = it.getMetadata().getName();
//            kubernetesClient.pods()
//                    .inNamespace("default")
//                    .withName(name)
//                    .writingOutput(System.out)
//                    .writingError(System.err)
//                    .withTTY()
//                    .exec(cmd);
//        });
//    }
//
//    /**
//     * 下载文件
//     */
//    public void download(String labelKey, String labelValue, String file, String saveFile) {
//        podClient.withLabel(labelKey, labelValue).list().getItems().forEach(it -> {
//            String name = it.getMetadata().getName();
//            kubernetesClient.pods()
//                    .inNamespace("default")
//                    .withName(name).file(file).copy(Paths.get(saveFile));
//        });
//        log.info("download finish");
//    }
//
//    /**
//     * 上传文件
//     */
//    public void upload(String labelKey, String labelValue, String uploadFile, String file) {
//        podClient.withLabel(labelKey, labelValue).list().getItems().forEach(it -> {
//            String name = it.getMetadata().getName();
//            kubernetesClient.pods()
//                    .inNamespace("default")
//                    .withName(name).file(file)
//                    .upload(Paths.get(uploadFile));
//        });
//        log.info("upload finish");
//    }
//
//    public void deletePod() {
//        List<Pod> pods = podClient.withLabel("app", "nginx").list().getItems();
//        if (pods.size() > 0) {
//            Pod pod = pods.get(random.nextInt(pods.size()));
//            log.info("delete pod:{}", pod);
//        }
//    }
//
//    /**
//     * check 状态
//     * Available=True
//     */
//    public void deploymentStats() {
//        DeploymentStatus status = deploymentClient.inNamespace("default").withName("mysql-deployment").get().getStatus();
//        log.info("mysql-deployment : status:{}", status.getConditions().stream().collect(Collectors.toMap(v -> v.getType(), v -> v.getStatus())));
//    }
//
//    public void createConfigMap() {
//        Yaml yaml = new Yaml();
//        InputStream inputStream = this.getClass().getResourceAsStream("/configmap/test_configmap.yaml");
//        ConfigMap configMap = yaml.loadAs(inputStream, ConfigMap.class);
//        configMapClient.create(configMap);
//    }
//
//    public void patchDeployment() {
//        Yaml yaml = new Yaml();
//        InputStream inputStream = this.getClass().getResourceAsStream("/deployment/redis_deployment.yaml");
//        Deployment deployment = yaml.loadAs(inputStream, Deployment.class);
//        deploymentClient.create(deployment);
//
//    }
//
//    public void containerNames() {
//        Pod pod = podClient.withLabels(ImmutableMap.of("app", "mysql")).list().getItems().get(0);
//        List<String> names = pod.getStatus().getContainerStatuses().stream().map(it -> it.getName()).collect(Collectors.toList());
//        log.info("pod:{}", names);
//
//        names.stream().forEach(name -> {
//            kubernetesClient.pods()
//                    .inNamespace("default")
//                    .withName(pod.getMetadata().getName())
//                    .inContainer(name)
//                    .writingOutput(System.out)
//                    .writingError(System.err)
//                    .withTTY()
//                    .exec("touch", "/tmp/eee");
//            log.info("name:{} container:{}", pod.getMetadata().getName(), name);
//        });
//    }
//
//
//    /**
//     * 添加sidecar
//     */
//    public void sidecar() {
//        Yaml yaml = new Yaml();
//        InputStream inputStream = this.getClass().getResourceAsStream("/deployment/redis_deployment.yaml");
//        Deployment deployment = yaml.loadAs(inputStream, Deployment.class);
//        InputStream sidecarIs = this.getClass().getResourceAsStream("/sidecar/tools.yaml");
//        Container container = yaml.loadAs(sidecarIs, Container.class);
//        deployment.getSpec().getTemplate().getSpec().getContainers().add(container);
//        deploymentClient.create(deployment);
//    }
//
//
//}
