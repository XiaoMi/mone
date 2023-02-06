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

package run.mone.docean.plugin.k8s.test.handler;

import com.xiaomi.youpin.docean.anno.Component;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import run.mone.docean.plugin.k8s.test.bo.Mone;
import run.mone.docean.plugin.k8s.test.bo.MoneList;

/**
 * @author goodjava@qq.com
 * @date 2022/5/29
 * 自定义资源
 */
@Slf4j
@Component(name = "moneResourceEventHandler")
@Data
@NoArgsConstructor
public class MoneResourceEventHandler implements ResourceEventHandler<Mone> {

    @javax.annotation.Resource
    private KubernetesClient kubernetesClient;


    @javax.annotation.Resource(name = "moneClient")
    private MixedOperation<Mone, MoneList, Resource<Mone>> moneClient;


    @Override
    public void onAdd(Mone mone) {
        int size = moneClient.list().getItems().size();
        log.info("add mone:{} size:{}", mone, size);
    }

    @Override
    public void onUpdate(Mone oldObj, Mone newObj) {
        log.info("update mone:{}", newObj);
    }

    @Override
    public void onDelete(Mone mone, boolean deletedFinalStateUnknown) {
        log.info("delete mone:{}", mone.getSpec());
    }
}
