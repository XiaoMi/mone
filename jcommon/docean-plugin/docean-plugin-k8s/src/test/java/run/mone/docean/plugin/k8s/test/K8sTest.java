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

package run.mone.docean.plugin.k8s.test;

import com.xiaomi.youpin.docean.Ioc;
import lombok.SneakyThrows;
import org.junit.Test;
import run.mone.docean.plugin.k8s.K8sPlugin;

/**
 * @author goodjava@qq.com
 * @date 2022/5/29
 */
public class K8sTest {

    @SneakyThrows
    @Test
    public void testCrd() {
        Ioc.ins().init("run.mone.docean.plugin","com.xiaomi.youpin.docean.plugin.config");
        Thread.currentThread().join();
    }
    @SneakyThrows
    @Test
    public void testKill() {
        String podName = "kibana-7f5fb9c49b-4gzck";
        String namespace = "xxx";
        K8sPlugin.KillContainer(namespace, podName,"kibana");
    }
}
