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

package run.mone.hera.operator.common;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Component;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.extern.slf4j.Slf4j;
import run.mone.hera.operator.bo.HeraBootstrap;
import run.mone.hera.operator.bo.HeraBootstrapList;
import run.mone.hera.operator.bo.HeraResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author shanwb
 * @date 2023-02-24
 */
@Component
@Slf4j
public class K8sUtilBean {
    private Gson gson = new Gson();
    @javax.annotation.Resource
    private KubernetesClient kubernetesClient;

    @javax.annotation.Resource(name = "heraClient")
    private MixedOperation<HeraBootstrap, HeraBootstrapList, Resource<HeraBootstrap>> heraClient;

    @javax.annotation.Resource(name = "deploymentClient")
    private MixedOperation<Deployment, DeploymentList, Resource<Deployment>> deploymentClient;
    public void applyResource(HeraResource heraResource, ObjectMeta objectMeta, String action) {
        if (null == heraResource.getNeedCreate() || !heraResource.getNeedCreate()) {
            return;
        }

        if (ResourceTypeEnum.SERVICE_CHECK.getTypeName().equals(heraResource.getResourceType())) {
            log.warn("SERVICE_CHECK type resource no need to apply");
            return;
        }
        String namespace = objectMeta.getNamespace();
        String yaml = heraResource.getYamlStr();

        applyYaml(yaml, namespace, action);
    }

    public void applyYaml(String yaml, String namespace, String action) {
        log.warn("applyYaml action:{}, ply yaml:{}", action, yaml);
        try (InputStream inputStream = new ByteArrayInputStream(yaml.getBytes("UTF-8"))) {
            ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> metaDataes = kubernetesClient.load(inputStream);
            List<HasMetadata> hasMetadataList = metaDataes.get();

            for (HasMetadata hasMetadata : hasMetadataList) {
                if (hasMetadata instanceof io.fabric8.kubernetes.api.model.Service) {
                    io.fabric8.kubernetes.api.model.Service heraService = (io.fabric8.kubernetes.api.model.Service) hasMetadata;
                    if ("delete".equals(action)) {
                        kubernetesClient.services().inNamespace(namespace).delete(heraService);
                    } else {
                        kubernetesClient.services().inNamespace(namespace).createOrReplace(heraService);
                    }
                    continue;
                }

                if (hasMetadata instanceof io.fabric8.kubernetes.api.model.PersistentVolume) {
                    io.fabric8.kubernetes.api.model.PersistentVolume heraPv = (io.fabric8.kubernetes.api.model.PersistentVolume) hasMetadata;
                    if ("delete".equals(action)) {
                        kubernetesClient.persistentVolumes().delete(heraPv);
                    } else {
                        kubernetesClient.persistentVolumes().createOrReplace(heraPv);
                    }
                    continue;
                }

                if (hasMetadata instanceof io.fabric8.kubernetes.api.model.storage.StorageClass) {
                    io.fabric8.kubernetes.api.model.storage.StorageClass heraStorageClass = (io.fabric8.kubernetes.api.model.storage.StorageClass) hasMetadata;
                    if ("delete".equals(action)) {
                        kubernetesClient.storage().storageClasses().delete(heraStorageClass);
                    } else {
                        kubernetesClient.storage().storageClasses().createOrReplace(heraStorageClass);
                    }
                    continue;
                }

                if (hasMetadata instanceof io.fabric8.kubernetes.api.model.PersistentVolumeClaim) {
                    io.fabric8.kubernetes.api.model.PersistentVolumeClaim heraPvc = (io.fabric8.kubernetes.api.model.PersistentVolumeClaim) hasMetadata;
                    if ("delete".equals(action)) {
                        kubernetesClient.persistentVolumeClaims().inNamespace(namespace).delete(heraPvc);
                    } else {
                        kubernetesClient.persistentVolumeClaims().inNamespace(namespace).createOrReplace(heraPvc);
                    }
                    continue;
                }

                if (hasMetadata instanceof io.fabric8.kubernetes.api.model.apps.Deployment) {
                    io.fabric8.kubernetes.api.model.apps.Deployment heraDeployment = (io.fabric8.kubernetes.api.model.apps.Deployment) hasMetadata;
                    if ("delete".equals(action)) {
                        deploymentClient.inNamespace(namespace).delete(heraDeployment);
                    } else {
                        Deployment deployment = deploymentClient.inNamespace(namespace).createOrReplace(heraDeployment);
                        log.warn("###deployment status:{}", gson.toJson(deployment.getStatus()));
                    }
                    continue;
                }

                if (hasMetadata instanceof io.fabric8.kubernetes.api.model.rbac.ClusterRoleBinding) {
                    io.fabric8.kubernetes.api.model.rbac.ClusterRoleBinding heraClusterRoleBinding = (io.fabric8.kubernetes.api.model.rbac.ClusterRoleBinding) hasMetadata;
                    if ("delete".equals(action)) {
                        kubernetesClient.rbac().clusterRoleBindings().delete(heraClusterRoleBinding);
                    } else {
                        kubernetesClient.rbac().clusterRoleBindings().createOrReplace(heraClusterRoleBinding);
                    }
                    continue;
                }

                if (hasMetadata instanceof io.fabric8.kubernetes.api.model.rbac.ClusterRole) {
                    io.fabric8.kubernetes.api.model.rbac.ClusterRole heraClusterRole = (io.fabric8.kubernetes.api.model.rbac.ClusterRole) hasMetadata;
                    if ("delete".equals(action)) {
                        kubernetesClient.rbac().clusterRoles().delete(heraClusterRole);
                    } else {
                        kubernetesClient.rbac().clusterRoles().createOrReplace(heraClusterRole);
                    }
                    continue;
                }

                if (hasMetadata instanceof io.fabric8.kubernetes.api.model.ServiceAccount) {
                    io.fabric8.kubernetes.api.model.ServiceAccount heraServiceAccount = (io.fabric8.kubernetes.api.model.ServiceAccount) hasMetadata;
                    if ("delete".equals(action)) {
                        kubernetesClient.serviceAccounts().delete(heraServiceAccount);
                    } else {
                        kubernetesClient.serviceAccounts().createOrReplace(heraServiceAccount);
                    }
                    continue;
                }

                if (hasMetadata instanceof io.fabric8.kubernetes.api.model.ConfigMap) {
                    io.fabric8.kubernetes.api.model.ConfigMap heraConfigMap = (io.fabric8.kubernetes.api.model.ConfigMap) hasMetadata;
                    if ("delete".equals(action)) {
                        kubernetesClient.configMaps().inNamespace(namespace).delete(heraConfigMap);
                    } else {
                        kubernetesClient.configMaps().inNamespace(namespace).createOrReplace(heraConfigMap);
                    }
                    continue;
                }

                if (hasMetadata instanceof io.fabric8.kubernetes.api.model.apps.DaemonSet) {
                    io.fabric8.kubernetes.api.model.apps.DaemonSet heraDaemonSet = (io.fabric8.kubernetes.api.model.apps.DaemonSet) hasMetadata;
                    if ("delete".equals(action)) {
                        kubernetesClient.apps().daemonSets().delete(heraDaemonSet);
                    } else {
                        kubernetesClient.apps().daemonSets().createOrReplace(heraDaemonSet);
                    }
                    continue;
                }

                if (hasMetadata instanceof io.fabric8.kubernetes.api.model.apps.StatefulSet) {
                    io.fabric8.kubernetes.api.model.apps.StatefulSet heraStatefulSet = (io.fabric8.kubernetes.api.model.apps.StatefulSet) hasMetadata;
                    if ("delete".equals(action)) {
                        kubernetesClient.apps().statefulSets().delete(heraStatefulSet);
                    } else {
                        kubernetesClient.apps().statefulSets().createOrReplace(heraStatefulSet);
                    }
                    continue;
                }

                log.warn("not support k8s kind:{}, yaml:{}", hasMetadata.getKind(), gson.toJson(hasMetadata.getMetadata()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
