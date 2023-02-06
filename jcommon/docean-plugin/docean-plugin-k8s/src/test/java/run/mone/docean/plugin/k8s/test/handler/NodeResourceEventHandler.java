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
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2022/5/29
 */
@Component(name = "nodeResourceEventHandler")
@Slf4j
public class NodeResourceEventHandler implements ResourceEventHandler<Node> {
    @Override
    public void onAdd(Node obj) {
        log.info("add node:{}", obj);
    }

    @Override
    public void onUpdate(Node oldObj, Node newObj) {
        log.info("node update old:{} new:{}", oldObj, newObj);
    }

    @Override
    public void onDelete(Node obj, boolean deletedFinalStateUnknown) {
        log.info("delete node:{}", obj);
    }
}
