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

package com.xiaomi.youpin.k8s;

import com.google.gson.Gson;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.PatchUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author gaoyibo
 * @author goodjava@qq.com
 */
public class K8s {
    private ApiClient client;

    public K8s() {
        this(null);
    }

    public K8s(String file) {
        try {
            if (StringUtils.isNotEmpty(file)) {
                client = Config.fromConfig(file);
            } else {
                client = ClientBuilder.standard().build();
            }
        } catch (Exception e) {
            client = null;
        } finally {
            Configuration.setDefaultApiClient(client);
//            client.setDebugging(true);
        }
    }

    public K8s(String url,String token) {
        try {
            if (StringUtils.isNotEmpty(url)) {
                client = Config.fromToken(url, token, false);
            } else {
                client = ClientBuilder.standard().build();
            }
        } catch (Exception e) {
            client = null;
        } finally {
            Configuration.setDefaultApiClient(client);
            // client.setDebugging(true);
        }
    }

    public K8s(String url,String uName, String upw) {
        try {
            if (StringUtils.isNotEmpty(url)) {
                client = Config.fromUserPassword(url, uName, upw, false);
            } else {
                client = ClientBuilder.standard().build();
            }
        } catch (Exception e) {
            client = null;
        } finally {
            Configuration.setDefaultApiClient(client);
            // client.setDebugging(true);
        }
    }

    /**
     * @param namespace 命名空间
     * @param name      deployment 名
     * @param replicas  扩容、缩容的个数
     * @return
     */
    public V1Scale scale(String namespace, String name, Integer replicas) throws IOException, ApiException {
        AppsV1Api appsV1Api = new AppsV1Api(client);
        String jsonPatchStr = "[{\"op\":\"replace\",\"path\":\"/spec/replicas\", \"value\": " + replicas + " }]";
        V1Patch patch = new V1Patch(jsonPatchStr);
        return appsV1Api.patchNamespacedDeploymentScale(name, namespace, patch, null, null, null, null);
    }

    /**
     * @param namespace 命名空间
     * @param name      deployment 名
     * @param imageName 镜像名: riskers/k8s-demo:0.1
     * @return
     */
    public V1Deployment createDeployment(String namespace, String name, String imageName) throws IOException, ApiException {
        String jsonDeploymentStr = "{\"kind\":\"Deployment\",\"apiVersion\":\"apps/v1\",\"metadata\":{\"name\":\"" + name + "\",\"finalizers\":[\"example.com/test\"],"
                + "\"labels\":{\"run\":\"hello-node\"}},\"spec\":{\"replicas\":1,\"selector\":{\"matchLabels\":{\"run\":\"hello-node\"}},"
                + "\"template\":{\"metadata\":{\"creationTimestamp\":null,\"labels\":{\"run\":\"hello-node\"}},\"spec\":{\"terminationGracePeriodSeconds\":30,"
                + "\"containers\":[{\"name\":\"hello-node\",\"imageName\":\"" + imageName + "\",\"ports\":[{\"containerPort\":8080,\"protocol\":\"TCP\"}],\"resources\":{}}]}},"
                + "\"strategy\":{}},\"status\":{}}";

        AppsV1Api appsV1Api = new AppsV1Api(client);

        V1Deployment body = Configuration.getDefaultApiClient().getJSON().deserialize(jsonDeploymentStr, V1Deployment.class);
        return appsV1Api.createNamespacedDeployment(namespace, body, null, null, null);
    }

    /**
     * namespace 下所有 deployment
     *
     * @param namespace
     * @return
     * @throws IOException
     * @throws ApiException
     */
    public V1DeploymentList deploymentList(String namespace, String labelSelector) throws IOException, ApiException {
        AppsV1Api appsV1Api = new AppsV1Api(client);
        return appsV1Api.listNamespacedDeployment(namespace, null, null, null, null, labelSelector, null, null, null, null);
    }

    /**
     * 创建 deployment
     *
     * @param namespace
     * @return
     * @throws IOException
     * @throws ApiException
     */
    public V1Deployment createDeployment(String namespace, String json) throws IOException, ApiException {
        V1Deployment v = new Gson().fromJson(json, V1Deployment.class);
        AppsV1Api appsV1Api = new AppsV1Api(client);
        V1Deployment body = Configuration.getDefaultApiClient().getJSON().deserialize(json, V1Deployment.class);
        return appsV1Api.createNamespacedDeployment(namespace, body, "false", null, null);
    }


    public V1Deployment createDeployment(String namespace, V1Deployment bo) throws IOException, ApiException {
        AppsV1Api appsV1Api = new AppsV1Api(client);
        return appsV1Api.createNamespacedDeployment(namespace, bo, "false", null, null);
    }

    /**
     * 创建服务
     *
     * @param namespace
     * @param json
     * @return
     * @throws IOException
     * @throws ApiException
     */
    public V1Service createService(String namespace, String json) throws IOException, ApiException {
        V1Service body = Configuration.getDefaultApiClient().getJSON().deserialize(json, V1Service.class);
        CoreV1Api api = new CoreV1Api(client);
        return api.createNamespacedService(namespace, body, null, null, null);
    }

    public V1Service createService(String namespace, V1Service bo) throws IOException, ApiException {
        CoreV1Api api = new CoreV1Api(client);
        return api.createNamespacedService(namespace, bo, null, null, null);
    }

    /**
     * 查询服务列表
     *
     * @param namespace
     * @return
     * @throws IOException
     * @throws ApiException
     */
    public V1ServiceList serviceList(String namespace) throws IOException, ApiException {
        CoreV1Api api = new CoreV1Api(client);
        return api.listNamespacedService(namespace, null, false, null, null, null, 10, null, null, null);
    }

    public V1Node patchNode(String name, String value) throws IOException, ApiException {
        CoreV1Api api = new CoreV1Api(client);
        V1Patch v1Patch = new V1Patch(value);
        return api.patchNode(name, v1Patch, null, null, null, null);
    }

    public V1NamespaceList getNamespaces() throws IOException, ApiException {
        CoreV1Api api = new CoreV1Api(client);
        return api.listNamespace(null, null, null, null, null, null, null, null, null);
    }

    /**
     * 删除指定服务
     *
     * @param namespace
     * @param name
     * @return
     * @throws IOException
     * @throws ApiException
     */
    public V1Status deleteService(String namespace, String name) throws IOException, ApiException {
        CoreV1Api api = new CoreV1Api(client);
        return api.deleteNamespacedService(name, namespace, null, null, null, null, null, null);
    }

    /**
     * 查询指定namespace下的所有pod
     *
     * @param namespace
     * @return
     * @throws IOException
     * @throws ApiException
     */
    public V1PodList listPod(String namespace, int limit) throws IOException, ApiException {
        CoreV1Api api = new CoreV1Api(client);
        return api.listNamespacedPod(namespace, null, false, null, null, null, limit, null, null, null);
    }

    public V1PodList listPod(String namespace, String fieldSelector, String labelSelector, Integer limit) throws IOException, ApiException {
        CoreV1Api api = new CoreV1Api(client);
        return api.listNamespacedPod(namespace, null, false, null, fieldSelector, labelSelector, limit, null, null, null);
    }

    public V1Pod deletePod(String namespace, String name) throws IOException, ApiException {
        CoreV1Api api = new CoreV1Api(client);
        return api.deleteNamespacedPod(name, namespace, null, null, null, null, null, null);
    }

    /**
     * 获取node列表
     *
     * @param limit
     * @return
     * @throws IOException
     * @throws ApiException
     */
    public V1NodeList listNode(String labelSelector, int limit) throws IOException, ApiException {
        CoreV1Api api = new CoreV1Api(client);
        return api.listNode(null, null, null, null, labelSelector, limit, null, null, null);
    }

    /**
     * 删除发布
     *
     * @param namespace
     * @param name
     * @return
     * @throws IOException
     * @throws ApiException
     */
    public V1Status deleteDeployment(String namespace, String name) throws IOException, ApiException {
        AppsV1Api appsV1Api = new AppsV1Api(client);
        return appsV1Api.deleteNamespacedDeployment(name, namespace, null, null, null, null, null, null);
    }

    /**
     * config map list
     *
     * @param namespace
     * @return
     * @throws ApiException
     * @throws IOException
     */
    public V1ConfigMapList configMapList(String namespace) throws ApiException, IOException {
        CoreV1Api api = new CoreV1Api(client);

        return api.listNamespacedConfigMap(namespace, null, null, null, null, null, null, null, null, null);
    }

    /**
     * 创建 config map 配置
     *
     * @param namespace
     * @param configName
     * @param data
     * @throws ApiException
     * @throws IOException
     */
    public void createConfigMap(String namespace, String configName, Map<String, String> data) throws ApiException, IOException {
        CoreV1Api api = new CoreV1Api(client);

        V1ConfigMap configMap = (new V1ConfigMapBuilder()).withApiVersion("v1").withKind("ConfigMap").withNewMetadata().withName(configName).withNamespace(namespace)
                .endMetadata().withData(data).build();

        api.createNamespacedConfigMap(namespace, configMap, null, null, null);
    }

    /**
     * 创建 secret
     *
     * @param namespace
     * @param secretName
     * @param data
     * @throws IOException
     * @throws ApiException
     */
    public void createSecret(String namespace, String secretName, Map<String, byte[]> data) throws IOException, ApiException {
        CoreV1Api api = new CoreV1Api(client);


        V1Secret body = new V1SecretBuilder()
                .withApiVersion("v1")
                .withKind("Secret")
                .withNewMetadata()
                .withName(secretName)
                .endMetadata()
                .withData(data)
                .build();

        body.setData(data);

        api.createNamespacedSecret(namespace, body, null, null, null);
    }

    public V1SecretList secretList(String namespace) throws ApiException, IOException {
        CoreV1Api api = new CoreV1Api(client);

        return api.listNamespacedSecret(namespace, null, null, null, null, null, null, null, null, null);
    }

    /**
     * @param namespace
     * @param name
     * @return
     * @throws IOException
     * @throws ApiException
     */
    public V1Deployment patchDeploymentYaml(String namespace, String name, String data) throws IOException, ApiException {
        AppsV1Api api = new AppsV1Api(client);
//        api.getApiClient().setDebugging(true);
        return PatchUtils.patch(V1Deployment.class,
                () ->
                        api.patchNamespacedDeploymentCall(
                                name,
                                namespace,
                                new V1Patch(data),
                                null,
                                null,
                                "example-field-manager", // field-manager is required for server-side apply
                                true,
                                null),
                V1Patch.PATCH_FORMAT_APPLY_YAML,
                api.getApiClient());
    }


    public void patchNamespacedDeployment(String namespace, String name, String value) throws IOException, ApiException {
        AppsV1Api api = new AppsV1Api(client);
        V1Patch patch = new V1Patch(value);

        V1Deployment result = api.readNamespacedDeployment(name, namespace, null, null, null);
        System.out.println(result);

        api.patchNamespacedDeployment(name, namespace, patch, null, null, null, null);
    }

    public V1Deployment patchDeploymentJson(String namespace, String name, String value) throws IOException, ApiException {
        AppsV1Api api = new AppsV1Api(client);
//        api.getApiClient().setDebugging(true);
        return PatchUtils.patch(
                V1Deployment.class,
                () ->
                        api.patchNamespacedDeploymentCall(
                                name,
                                namespace,
                                new V1Patch(value),
                                null,
                                null,
                                null, // field-manager is optional
                                null,
                                null),
                V1Patch.PATCH_FORMAT_JSON_PATCH,
                api.getApiClient());
    }

    public V1ReplicaSetList replicaSetList(String namespace, String filedSelector, String labelSelector) throws IOException, ApiException {
        AppsV1Api api = new AppsV1Api(client);
        return api.listNamespacedReplicaSet(namespace, null, null, null, filedSelector, labelSelector, null, null, null, null);
    }
}
