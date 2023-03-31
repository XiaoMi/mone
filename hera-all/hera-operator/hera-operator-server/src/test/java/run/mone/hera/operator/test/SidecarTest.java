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
package run.mone.hera.operator.test;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2022/6/14 15:56
 */
public class SidecarTest {

    @Test
    public void testSidecar() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass().getResourceAsStream("/xxx/deployment/redis_deployment.yaml");
        Deployment deployment = yaml.loadAs(inputStream, Deployment.class);
        List<Container> containers = deployment.getSpec().getTemplate().getSpec().getContainers();
        InputStream sidecarIs = this.getClass().getResourceAsStream("/xxx/sidecar/tools.yaml");
        Container container = yaml.loadAs(sidecarIs, Container.class);
        containers.add(container);
        System.out.println(deployment);
    }


    @Test
    public void saa() {
        Yaml yaml = new Yaml();
        Container container = new Container();
        container.setName("abc");
        System.out.println(yaml.dump(container));
    }


}
