package com.xiaomi.youpin.k8s;

import com.google.gson.Gson;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaoyibo
 * @author goodjava@qq.com
 */
public class K8sTest {

    // private final String namespace = "default";
    private final String namespace = "mione-staging";

    @Test
    public void test1() throws IOException, ApiException {
        V1Deployment res = new K8s().createDeployment("default", "xx", "riskers/k8s-demo:0.1");
        System.out.println(res);
    }

    @Test
    public void testGson() {
        V1Deployment v = new V1Deployment();
        V1ObjectMeta m = new V1ObjectMeta();
        m.setName("abc");
        v.setMetadata(m);
        System.out.println(new Gson().toJson(v));
        V1Deployment v1 = new Gson().fromJson(new Gson().toJson(v), V1Deployment.class);
        System.out.println(v1);
    }

    @Test
    public void testPatchNode() throws IOException, ApiException {
        new K8s().patchNode("docker-desktop", "\"metadata\": {\n" +
                "        \"labels\": {\n" +
                "            \"age\": \"22\"}\n" +
                "        }");
    }

    @Test
    public void testPatchNamespacedDeployment() throws IOException, ApiException {
        int replicas = 2;
        String jsonPatchStr = "[{\"op\":\"replace\",\"path\":\"/spec/replicas\", \"value\": " + replicas + " }]";
        new K8s().patchDeploymentJson(namespace, "zzytest-163-deployment", jsonPatchStr);
    }

    /**
     * 创建nacos 发布
     * /Users/zhangzhiyong/IdeaProjects/mione/jcommon/k8s/src/test/resources/k8s-demo/k8s/deploment-mysql.json
     * /Users/zzy/IdeaProjects/
     *
     * @throws IOException
     * @throws ApiException
     */
    @Test
    public void testCreateDeploment() throws IOException, ApiException {
        // String name = "nacos";
        String name = "redis";
        // String name = "mysql";
        // String name = "zzytest";
        // String name = "bookstore";
        // String name = "nginx";
        // String name = "flink";
        V1Deployment res = new K8s().createDeployment(namespace,
                new String(
                        Files.readAllBytes(
                                Paths.get(String.format(
                                        "/home/kiririn/src/java/mione/jcommon/k8s/src/test/resources/k8s-demo/k8s/deploment-%s.json",
                                        name)))));
        System.out.println(res);
    }

    /**
     * 支持操作
     * replace
     * [{"op": "replace", "path": "/spec/containers/0/image", "value":"new image"}]
     * add
     * [{"op": "add", "path": "/metadata/labels/MixedDeploy_Status",
     * "value":"true"}]
     * remove
     * [{"op": "remove", "path": "/metadata/labels/MixedDeploy_Status"}]
     *
     * @throws IOException
     * @throws ApiException
     */
    @Test
    public void testPatchNode2() throws IOException, ApiException {
        new K8s().patchNode("node",
                "[{\"op\": \"add\", \"path\": \"/metadata/labels/name\", \"value\":\"tom\"}]");
    }

    /**
     * 创建nacos 服务
     *
     * @throws IOException
     * @throws ApiException
     */
    @Test
    public void testCreateService() throws IOException, ApiException {
        // String name = "nacos";
        // String name = "redis";
        // String name = "mysql";
        String name = "zzytest";
        // String name = "bookstore";
        // String name = "nginx";
        // String lb = "-lb";
        String lb = "";
        V1Service res = new K8s().createService(namespace, new String(Files.readAllBytes(Paths.get(
                String.format(
                        "/opt/IdeaProjects/mione/jcommon/k8s/src/test/resources/k8s-demo/k8s/service"
                                + lb + "-%s.json",
                        name)))));
        System.out.println(res);
    }

    @Test
    public void testListNode() throws IOException, ApiException {
        V1NodeList list = new K8s().listNode("name=zzy", 3);
        list.getItems().forEach(it -> {
            System.out.println(it.getMetadata().getLabels());
        });
    }

    /**
     * 获取服务列表
     *
     * @throws IOException
     * @throws ApiException
     */
    @Test
    public void testServiceList() throws IOException, ApiException {
        V1ServiceList list = new K8s().serviceList("default");
        System.out.println(list);
    }

    /**
     * 删除服务
     *
     * @throws IOException
     * @throws ApiException
     */
    @Test
    public void testDeleteService() throws IOException, ApiException {
        // String name = "nginx-service";
        String name = "nacos-service";
        V1Status res = new K8s().deleteService("default", "nacos-service");
        System.out.println(res);
    }

    /**
     * 查询pod 列表
     *
     * @throws IOException
     * @throws ApiException
     */
    @Test
    public void testListPod() throws IOException, ApiException {
        V1PodList list = new K8s().listPod(namespace, 10);
        System.out.println(list);
        list.getItems().stream().forEach(it -> {
            System.out.println("---->" + it.getMetadata().getLabels().get("app"));
        });
    }

    @Test
    public void testListPod2() throws IOException, ApiException {
        V1PodList list = new K8s().listPod(namespace, null, "app=zzytest-163-server", null);
        // System.out.println(list);
        list.getItems().stream().forEach(it -> {
            System.out.println("---->" + it.getMetadata().getName() + "---" + it.getStatus().getPodIP());
        });
    }

    @Test
    public void testDeletePod() throws IOException, ApiException {
        V1Pod pod = new K8s().deletePod(namespace, "zzytest-163-deployment-7db794f449-fj298");
        System.out.println(pod);
    }

    @Test
    public void testScale() throws IOException, ApiException {
        new K8s().scale(namespace, "zzytest-163-deployment", 3);
    }

    @Test
    public void testDeploymentList() throws IOException, ApiException {
        V1DeploymentList list = new K8s().deploymentList(namespace, "app=zzytest-163");
        list.getItems().stream().forEach(it -> {
            System.out.println(it.getMetadata().getName());
        });
    }

    @Test
    public void testDeleteDeployment() throws IOException, ApiException {
        V1Status status = new K8s().deleteDeployment("default", "nacos-deployment");
        System.out.println(status);
    }

    @Test
    public void testConfigPublish() throws IOException, ApiException {
        Map<String, String> data = new HashMap<>();
        data.put("key1", "value4");
        data.put("key2", "value4");
        new K8s().createConfigMap("default", "mione-config", data);
    }

    @Test
    public void testConfigMapList() throws IOException, ApiException {
        V1ConfigMapList responses = new K8s().configMapList("default");
        System.out.println(responses);
    }

    @Test
    public void testCreateSecret() throws IOException, ApiException {
        Map<String, byte[]> data = new HashMap();
        data.put("name", "zzy".getBytes());
        new K8s().createSecret("default", "zzy-secret", data);
    }

    @Test
    public void testSecretList() throws IOException, ApiException {
        V1SecretList list = new K8s().secretList("default");
        System.out.println(list);
    }

    @Test
    public void testPodsOfIP() throws IOException, ApiException {
        List<V1Pod> list = new K8s().getPodsOfIP("127.0.0.1",null);
        System.out.println(list);
    }

    @Test
    public void testPatchDeploymentYaml() throws IOException, ApiException, InterruptedException {
        V1Deployment list = new K8s().patchDeploymentYaml("mione-on-k8s", "k8s-demo-deployment", new String(
                Files.readAllBytes(
                        Paths.get(
                                "jcommon/k8s/src/test/resources/k8s-demo/k8s/deployment.yaml"))));
        System.out.println(list);
        Thread.sleep(30000);

        list = new K8s().patchDeploymentYaml("mione-on-k8s", "k8s-demo-deployment", new String(
                Files.readAllBytes(
                        Paths.get(
                                "jcommon/k8s/src/test/resources/k8s-demo/k8s/deployment2.yaml"))));
        System.out.println(list);
        Thread.sleep(30000);
    }

    @Test
    public void testReplicaSet() throws IOException, ApiException, InterruptedException {
        System.out.println(new K8s().replicaSetList("mione-staging", null, "app=zzytest-163-server").getItems().size());
        System.out.println(new K8s().replicaSetList("mione-staging", null, "app=zzytest-163-server").getItems().get(0)
                .getStatus().getReplicas());
        System.out.println(new K8s().replicaSetList("mione-staging", null, "app=zzytest-163-server").getItems().get(1)
                .getStatus().getReplicas());
        DateTime time = new K8s().replicaSetList("mione-staging", null, "app=zzytest-163-server").getItems().get(0)
                .getMetadata().getCreationTimestamp();
        DateTime time1 = new K8s().replicaSetList("mione-staging", null, "app=zzytest-163-server").getItems().get(1)
                .getMetadata().getCreationTimestamp();
        System.out.println(time.isBefore(time1));
    }
}
